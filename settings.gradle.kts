plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.gradle.develocity") version "4.3"
}

rootProject.name = "RestBank"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}

includeBuild("build-logic")

include(
    "libs",
    "libs:api-contract"
)

include(
    "services:auth-service",
    "services:customer-service",
    "services:api-gateway",
    "services:card-service",
    "services:eureka-server"
)