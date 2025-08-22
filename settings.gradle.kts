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
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
