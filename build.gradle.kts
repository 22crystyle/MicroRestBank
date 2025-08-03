plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.14"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "com.example"
version = "2.0.0"
description = "restbank"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.liquibase.core)
    }
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

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.retry)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)

    implementation(libs.liquibase.core)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

openApi {
    apiDocsUrl.set("http://localhost:1024/v3/api-docs.yaml")
    outputDir.set(layout.projectDirectory.dir("docs"))
    outputFileName.set("openapi.yaml")
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
