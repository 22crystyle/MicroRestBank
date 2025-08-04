plugins {
    `java-library`
    id("java-convention")
}

group = "com.example"
version = "1.0.0"
description = "restbank"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "org.springdoc.openapi-gradle-plugin")

    group = "com.example.restbank"
    version = "0.0.1-SNAPSHOT"

    tasks.withType<Javadoc>() {
        options.encoding = "UTF-8"
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-nowarn")
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

tasks.configureEach { enabled = false }
tasks.named("clean") { enabled = true }