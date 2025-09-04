import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.JavaExec
import org.gradle.process.JavaForkOptions

plugins {
    base
    id("java-convention") apply false
    id("full-cycle-time") apply true
}

tasks.named("clean") {
    dependsOn(gradle.includedBuild("build-src").task(":clean"))
}

description = "restbank"

allprojects {
    group = "com.example.restbank"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

val apiServiceProjects = listOf(
    "api-gateway",
    "auth-service",
    "card-service",
    "customer-service"
)

val dockerEnvFile = file("docker/.env")
val dockerEnv = if (dockerEnvFile.exists()) {
    dockerEnvFile.readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .mapNotNull {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2 && parts[0].isNotBlank()) parts[0] to parts[1] else null
        }.toMap()
} else {
    emptyMap<String, String>()
}

val localApiGenerationEnv = dockerEnv.toMutableMap().apply {
    getOrPut("KEYCLOAK_ISSUER_URI") { "http://localhost:${this["KEYCLOAK_HTTP_PORT"]}/realms/bank-realm" }
    getOrPut("DB_JDBC_URL") { "jdbc:postgresql://localhost:${this["POSTGRES_PORT"]}/${this["POSTGRES_DB"]}" }
    getOrPut("KAFKA_BOOTSTRAP_SERVER") { "localhost:${this["KAFKA_PORT"]}" }
    getOrPut("EUREKA_SERVICE_URL") { "http://localhost:${this["EUREKA_SERVER_PORT"]}/eureka" }
}

subprojects {
    if (project.name != "build-src") {
        apply(plugin = "java-convention")

        version = "0.0.1-SNAPSHOT"

        tasks.withType<Delete> {
            delete("docs")
        }

        afterEvaluate {
            tasks.findByName("generateOpenApiDocs")?.let { task ->
                task.enabled = project.name in apiServiceProjects
            }
            if (project.name in apiServiceProjects) {
                tasks.withType<JavaExec> {
                    environment(localApiGenerationEnv)
                }
            }
        }
    }
}

tasks.register("generateAllApiDocs") {
    group = "Documentation"
    description = "Generates OpenAPI documentation for all applicable services."
    dependsOn(apiServiceProjects.map { ":services:$it:generateOpenApiDocs" })
}

project(":services:api-gateway") {
    configurations.named("implementation") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework", module = "spring-webmvc")
    }
}

project(":services:auth-service") {
    configurations.named("implementation") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework", module = "spring-webmvc")
    }
}

listOf(
    ":services:api-gateway",
    ":services:auth-service",
    ":services:card-service",
    ":services:customer-service"
).forEach { servicePath ->
    project(servicePath).afterEvaluate {
        tasks.findByName("forkedSpringBootRun")?.dependsOn(project(":shared").tasks.getByName("jar"))
    }
}