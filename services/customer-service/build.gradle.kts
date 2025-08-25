plugins {
    id("java")
    id("java-convention")
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(project(":shared"))
    implementation(libs.keycloak.admin.client)
    implementation(libs.keycloak.services)
    implementation(libs.spring.kafka)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
}

tasks.test {
    useJUnitPlatform()
}