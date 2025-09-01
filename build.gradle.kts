import javax.inject.Inject
import org.gradle.process.ExecOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.process.JavaForkOptions
import org.gradle.api.tasks.Delete

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

abstract class FullCycleTimeTask : DefaultTask() {

    @get:InputDirectory
    abstract val dockerDirectory: DirectoryProperty

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @TaskAction
    fun execute() {
        val startTime = System.currentTimeMillis()

        logger.lifecycle("Building JAR files...")
        execOperations.exec {
            commandLine("./gradlew.bat", "bootJar")
        }

        logger.lifecycle("Starting docker-compose...")
        execOperations.exec {
            workingDir = dockerDirectory.get().asFile
            commandLine("docker-compose", "down")
        }
        execOperations.exec {
            workingDir = dockerDirectory.get().asFile
            commandLine("docker-compose", "up", "-d")
        }

        logger.lifecycle("Waiting for api-gateway to be healthy...")
        var healthy = false
        val maxWaitTime = java.time.Duration.ofMinutes(5).toMillis()
        val waitInterval = java.time.Duration.ofSeconds(5).toMillis()
        val deadline = startTime + maxWaitTime

        while (System.currentTimeMillis() < deadline) {
            val standardOutput = java.io.ByteArrayOutputStream()
            val result = execOperations.exec {
                commandLine("curl", "--silent", "--write-out", "%{http_code}", "--output", "NUL", "http://localhost:1042/actuator/health")
                isIgnoreExitValue = true
                setStandardOutput(standardOutput)
            }

            val httpCode = standardOutput.toString().trim()

            if (httpCode == "200") {
                healthy = true
                logger.lifecycle("api-gateway is healthy! (HTTP 200)")
                break
            } else {
                if (result.exitValue != 0) {
                     logger.warn("Waiting for api-gateway... curl command failed with exit code ${result.exitValue}. Is curl installed and in your PATH?")
                } else {
                     logger.warn("Waiting for api-gateway... Received HTTP status ${httpCode}")
                }
            }
            Thread.sleep(waitInterval)
        }

        if (healthy) {
            logger.lifecycle("Services are up, generating API docs...")
            execOperations.exec {
                commandLine("./gradlew.bat", "generateAllApiDocs", "--no-configuration-cache")
            }
        }

        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime
        val duration = java.time.Duration.ofMillis(totalTime)
        val minutes = duration.toMinutes()
        val seconds = duration.seconds % 60
        val millis = duration.toMillis() % 1000

        if (!healthy) {
            logger.error("fullCycleTime failed after: ${minutes}m ${seconds}s ${millis}ms")
            throw GradleException("api-gateway did not become healthy within the timeout.")
        }

        logger.lifecycle("fullCycleTime completed in: ${minutes}m ${seconds}s ${millis}ms")
    }
}

tasks.register("fullCycleTime", FullCycleTimeTask::class.java) {
    group = "custom"
    description = "Runs the full cycle: clean, build, docker-compose up, and waits for services to be healthy."
    dockerDirectory.set(project.layout.projectDirectory.dir("docker"))
    dependsOn(project.subprojects.mapNotNull { it.tasks.findByName("clean") })
}