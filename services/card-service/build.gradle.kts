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

print("q")