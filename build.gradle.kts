plugins {
    `java-library`
    id("java-convention")
    id("io.freefair.lombok") version "8.14" apply false
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0" apply false
}

group = "com.example"
version = "1.0.0"
description = "restbank"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    runtimeOnly(libs.jjwt.api)
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

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "org.springdoc.openapi-gradle-plugin")

    group = "com.example.restbank"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://repo.maven.apache.org/maven2/")
        }
    }

    tasks.withType<Javadoc>() {
        options.encoding = "UTF-8"
    }
}

val agentJar: String by lazy {
    configurations.testRuntimeClasspath.get()
        .files
        .find { it.name.startsWith("byte-buddy-agent") }
        ?.absolutePath
        ?: error("byte-buddy-agent.jar not found in testRuntimeClasspath")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    forkEvery = 0
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    jvmArgs("-Xshare:off", "-javaagent:$agentJar")
}