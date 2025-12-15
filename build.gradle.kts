plugins {
    base
    id("full-cycle-time") apply true
}

tasks.named("clean") {
    dependsOn(gradle.includedBuild("build-logic").task(":clean"))
}

description = "restbank"

allprojects {
    group = "org.restbank"
    version = "0.0.1-SNAPSHOT"
}

val apiServiceProjects = setOf(
    ":services:api-gateway",
    ":services:auth-service",
    ":services:card-service",
    ":services:customer-service"
)

val dockerEnv = loadEnv(file("docker/.env"))

val localApiGenerationEnv = dockerEnv + mapOf(
    "KEYCLOAK_ISSUER_URI" to "http://localhost:${dockerEnv["KEYCLOAK_HTTP_PORT"]}/realms/bank-realm",
    "DB_JDBC_URL" to "jdbc:postgresql://localhost:${dockerEnv["POSTGRES_PORT"]}/${dockerEnv["POSTGRES_DB"]}",
    "KAFKA_BOOTSTRAP_SERVER" to "localhost:${dockerEnv["KAFKA_PORT"]}",
    "EUREKA_SERVICE_URL" to "http://localhost:${dockerEnv["EUREKA_SERVER_PORT"]}/eureka"
)

subprojects {
    if (!path.startsWith(":services:")) return@subprojects

    tasks.matching { it.name == "clean" }.configureEach {
        this as Delete
        delete(layout.projectDirectory.dir("docs"))
    }

    if (path in apiServiceProjects) {
        tasks.withType<JavaExec>().configureEach {
            environment(localApiGenerationEnv)
        }
    }
}

tasks.register("generateAllApiDocs") {
    group = "documentation"
    description = "Generates OpenAPI documentation for all API services"
    dependsOn(apiServiceProjects.map { "$it:generateOpenApiDocs" })
}

fun loadEnv(file: File): Map<String, String> =
    if (!file.exists()) emptyMap()
    else file.readLines()
        .asSequence()
        .map(String::trim)
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .mapNotNull {
            it.split("=", limit = 2)
                .takeIf { parts -> parts.size == 2 }
                ?.let { (k, v) -> k to v }
        }
        .toMap()

apiServiceProjects.forEach { servicePath ->
    project(servicePath).afterEvaluate {
        tasks.findByName("forkedSpringBootRun")?.dependsOn(project(":libs:api-contract").tasks.getByName("jar"))
    }
}