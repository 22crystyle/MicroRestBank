import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.InputDirectory
import org.gradle.process.ExecOperations
import org.gradle.process.JavaForkOptions
import javax.inject.Inject

plugins {
    base
    id("java-convention") apply false
    id("full-cycle-time") apply true
}

tasks.named<Delete>("clean") {
    delete(
        ".gradle",
        fileTree("build-src") {
            include("**/build", "**/.gradle")
        }
    )
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
    dockerEnvFile.readLines().mapNotNull {
        val parts = it.split("=", limit = 2)
        if (parts.size == 2 && parts[0].isNotBlank()) parts[0] to parts[1] else null
    }.toMap()
} else {
    emptyMap()
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
                tasks.all {
                    if (this is JavaForkOptions) {
                        environment.putAll(dockerEnv)
                    }
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

