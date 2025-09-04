
package com.example.restbank.build

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.InputDirectory
import org.gradle.process.ExecOperations
import org.gradle.process.JavaForkOptions
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction


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
            commandLine("./gradlew", "bootJar")
        }

        logger.lifecycle("Starting docker-compose...")
        execOperations.exec {
            workingDir = dockerDirectory.get().asFile
            commandLine("docker-compose", "down")
        }
        execOperations.exec {
            workingDir = dockerDirectory.get().asFile
            commandLine("docker-compose", "up", "--build", "--force-recreate", "-d")
        }

        logger.lifecycle("Waiting for api-gateway to be healthy...")
        var healthy = false
        val maxWaitTime = java.time.Duration.ofMinutes(5).toMillis()
        val waitInterval = java.time.Duration.ofSeconds(5).toMillis()
        val deadline = startTime + maxWaitTime

        while (System.currentTimeMillis() < deadline) {
            val standardOutput = java.io.ByteArrayOutputStream()
            val nullDevice = if (System.getProperty("os.name").lowercase().contains("windows")) "NUL" else "/dev/null"
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
                commandLine("./gradlew", "generateAllApiDocs", "--no-configuration-cache")
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
