rootProject.name = "RestBank"


include(
    "services:auth-service",
    "services:customer-service",
    "services:api-gateway",
    "services:card-service",
    "services:transaction-service",
    "services:notification-service",
    "services:statement-service"
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
include("shared")
