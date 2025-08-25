rootProject.name = "RestBank"


include(
    "services:auth-service",
    "services:customer-service",
    "services:api-gateway",
    "services:card-service",
    "services:eureka-server",
    "shared"
)

includeBuild("build-src")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
