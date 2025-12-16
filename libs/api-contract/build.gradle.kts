plugins {
    id("java")
    alias(libs.plugins.lombok)
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.swagger.annotations.jakarta)

    implementation(libs.mapstruct)
    implementation(libs.lombok.mapstruct.binding)
    implementation(libs.spring.security.oauth2.resource.server)
    implementation(libs.spring.doc.mvc)

    annotationProcessor(libs.mapstruct.processor)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}