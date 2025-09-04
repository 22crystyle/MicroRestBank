package com.example.restbank.build

import org.gradle.api.Plugin
import org.gradle.api.Project

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
