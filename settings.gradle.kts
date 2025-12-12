plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeBuild("build-logic")

rootProject.name = "RestBank"

include(
    "services:auth-service",
    "services:customer-service",
    "services:api-gateway",
    "services:card-service",
    "services:eureka-server",
    "shared"
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}
