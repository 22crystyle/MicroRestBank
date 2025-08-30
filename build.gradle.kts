plugins {
    `java-library`
    id("java-convention")
}

group = "com.example"
version = "1.0.0"
description = "restbank"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

val apiServiceProjects = listOf(
    "api-gateway",
    "auth-service",
    "card-service",
    "customer-service"
)

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")

    group = "com.example.restbank"
    version = "0.0.1-SNAPSHOT"

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-nowarn")
    }

    afterEvaluate {
        tasks.findByName("generateOpenApiDocs")?.let { task ->
            task.enabled = project.name in apiServiceProjects
        }
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

tasks.register("generateAllApiDocs") {
    group = "Documentation"
    description = "Generates OpenAPI documentation for all applicable services."
    dependsOn(apiServiceProjects.map { ":services:$it:generateOpenApiDocs" })
}

project(":services:api-gateway").afterEvaluate {
    configurations.implementation {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework", module = "spring-webmvc")
    }
}

project(":services:auth-service").afterEvaluate {
    configurations.implementation {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework", module = "spring-webmvc")
    }
}

listOf(
    ":services:api-gateway",
    ":services:auth-service",
    ":services:card-service",
    ":services:customer-service"
).forEach { servicePath ->
    project(servicePath).afterEvaluate {
        tasks.findByName("forkedSpringBootRun")?.dependsOn(project(":shared").tasks.getByName("jar"))
    }
}