import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

class FullCycleTimePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("fullCycleTime", FullCycleTimeTask::class.java) {
            group = "custom"
            description = "Runs the full cycle: clean, build, docker-compose up, and waits for services to be healthy."
            dockerDirectory.value(project.layout.projectDirectory.dir("docker"))
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

        val servicesToMonitor = listOf("api-gateway", "auth-service", "card-service", "customer-service")
        val allHealthy = servicesToMonitor.all { serviceName ->
            logger.lifecycle("Waiting for $serviceName to be healthy...")
            val containerName = "restbank-${serviceName}-1"
            val maxWaitTime = java.time.Duration.ofMinutes(2).toMillis()
            val waitInterval = java.time.Duration.ofSeconds(5).toMillis()
            val deadline = System.currentTimeMillis() + maxWaitTime
            var isHealthy = false

            while (System.currentTimeMillis() < deadline) {
                val standardOutput = java.io.ByteArrayOutputStream()
                val result = execOperations.exec {
                    commandLine("docker", "inspect", "--format", "'{{.State.Health.Status}}'", containerName)
                    isIgnoreExitValue = true
                    setStandardOutput(standardOutput)
                }

                val status = standardOutput.toString().trim().replace("'", "")

                if (status == "healthy") {
                    logger.lifecycle("$serviceName is healthy!")
                    isHealthy = true
                    break
                } else {
                    if (result.exitValue != 0) {
                        logger.warn("Waiting for $serviceName... 'docker inspect' failed. Container might not be running yet.")
                    } else {
                        logger.warn("Waiting for $serviceName... current status: $status")
                    }
                }
                Thread.sleep(waitInterval)
            }
            isHealthy
        }

        if (allHealthy) {
            logger.lifecycle("All services are healthy, generating API docs...")
            execOperations.exec {
                commandLine(gradlew, "generateAllApiDocs", "--no-configuration-cache")
            }
        } else {
            logger.error("Not all services became healthy within the timeout.")
            throw GradleException("One or more services did not become healthy within the timeout.")
        }
    }
}