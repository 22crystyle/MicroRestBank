plugins {
    `java-library`
    id("java-convention")
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.retry)
    implementation(libs.liquibase.core)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)

    implementation(libs.spring.cloud.openfeign)
    implementation(libs.resilience4j.spring.boot2)
    implementation(libs.spring.kafka)
    runtimeOnly(libs.resilience4j.feign)

    implementation(libs.spring.doc.mvc)
}

tasks.test {
    useJUnitPlatform()
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