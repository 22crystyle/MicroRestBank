plugins {
    id("java")
    id("java-convention")
}
val springCloudVersion by extra("2024.0.2")

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.spring.cloud.starter.circuitbreaker.reactor.resilience4j)
    implementation(libs.spring.cloud.starter.loadbalancer)
    testImplementation(libs.spring.cloud.starter.contract.stub.runner)
    testImplementation(libs.reactor.test)

    implementation(libs.spring.doc.flux)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.test {
    useJUnitPlatform()
}