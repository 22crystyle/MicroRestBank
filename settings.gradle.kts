rootProject.name = "RestBank"

include(
    "services:auth-service",
    "services:customer-service",
    "services:account-service",
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

    plugins {
        `java-library`
        id("io.freefair.lombok") version "8.14"
        id("org.springframework.boot") version "3.4.5"
        id("io.spring.dependency-management") version "1.1.7"
        id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    }
}
