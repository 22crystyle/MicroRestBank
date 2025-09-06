import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.process.ExecOperations
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class FullCycleTimePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("fullCycleTime", FullCycleTimeTask::class.java) {
            group = "custom"
            description = "Runs the full cycle: clean, build, docker-compose up, and waits for services to be healthy."
            dockerDirectory.set(project.layout.projectDirectory.dir("docker"))
            dependsOn(project.subprojects.mapNotNull { it.tasks.findByName("clean") })
        }
    }
}

abstract class FullCycleTimeTask : DefaultTask() {

    @get:InputDirectory
    abstract val dockerDirectory: DirectoryProperty

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @TaskAction
    fun execute() {
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        val gradlew = if (isWindows) "gradlew.bat" else "./gradlew"

        logger.lifecycle("Building JAR files...")
        execOperations.exec {
            commandLine(gradlew, "bootJar")
        }

        logger.lifecycle("Tearing down docker-compose...")
        execOperations.exec {
            workingDir = dockerDirectory.get().asFile
            commandLine("docker-compose", "-p", "restbank", "down", "--remove-orphans")
        }
        
        logger.lifecycle("Starting docker-compose...")
        execOperations.exec {
            workingDir = dockerDirectory.get().asFile
            commandLine("docker-compose", "-p", "restbank", "up", "--build", "--force-recreate", "-d")
        }

        logger.lifecycle("Waiting for api-gateway to be healthy...")
        var healthy = false
        val maxWaitTime = java.time.Duration.ofMinutes(2).toMillis()
        val waitInterval = java.time.Duration.ofSeconds(5).toMillis()
        val healthCheckStartTime = System.currentTimeMillis()
        val deadline = healthCheckStartTime + maxWaitTime

        while (System.currentTimeMillis() < deadline) {
            val standardOutput = java.io.ByteArrayOutputStream()
            val nullDevice = if (isWindows) "NUL" else "/dev/null"
            val result = execOperations.exec {
                commandLine(
                    "curl",
                    "--silent",
                    "--write-out",
                    "%{http_code}",
                    "--output",
                    nullDevice,
                    "http://localhost:1042/actuator/health"
                )
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
                commandLine(gradlew, "generateAllApiDocs", "--no-configuration-cache")
            }
        }

        if (!healthy) {
            throw GradleException("api-gateway did not become healthy within the timeout.")
        }
    }
}