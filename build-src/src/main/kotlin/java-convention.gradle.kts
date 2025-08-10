import org.gradle.accessors.dm.LibrariesForLibs

val libs = extensions.getByType<LibrariesForLibs>()

plugins {
    `java-library`
    id("io.freefair.lombok") apply false
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
    id("org.springdoc.openapi-gradle-plugin") apply false
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(libs.spring.boot.starter.actuator)

    implementation(libs.postgresql)
    implementation(libs.h2database)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security.oauth2.resource.server)
    annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor")

    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.swagger.annotations)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)

    /*implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)*/

    /*implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)

    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.retry)
    implementation(libs.spring.cloud.openfeign)


    implementation(libs.liquibase.core)*/
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.2")
    }
}