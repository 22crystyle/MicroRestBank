plugins {
    base
    id("java-convention") apply false
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

subprojects {
    if (project.name != "build-src") {
        apply(plugin = "java-convention")

        version = "0.0.1-SNAPSHOT"

        afterEvaluate {
            tasks.findByName("generateOpenApiDocs")?.let { task ->
                task.enabled = project.name in apiServiceProjects
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