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
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.swagger.annotations)

    implementation(libs.postgresql)
    implementation(libs.h2database)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.retry)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)

    implementation(libs.liquibase.core)
}