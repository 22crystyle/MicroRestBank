plugins {
    id("java")
    alias(libs.plugins.lombok)
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.swagger.annotations.jakarta)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)
    implementation("org.springframework.security:spring-security-oauth2-resource-server:6.4.5") //TODO: SonarQube

    implementation(libs.spring.doc.mvc)
}

tasks.test {
    useJUnitPlatform()
}