plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("full-cycle-time") {
            id = "full-cycle-time"
            implementationClass = "FullCycleTimePlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation(plugin(libs.plugins.spring.boot))
    implementation(plugin(libs.plugins.openapi))
    implementation(plugin(libs.plugins.spring.dependency))
}

fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }