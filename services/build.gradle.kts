plugins {
    id("buildlogic.java-service-conventions") apply false
    alias(libs.plugins.lombok) apply false
}

val lombok: String = libs.plugins.lombok.get().pluginId

allprojects {
    group = "org.restbank.services"
    version = rootProject.version
}

subprojects {
    apply(plugin = "buildlogic.java-service-conventions")
    plugins.apply(lombok)
}