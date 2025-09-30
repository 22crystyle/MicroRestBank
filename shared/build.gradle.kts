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
    implementation(libs.spring.boot.starter.web)
    implementation(libs.swagger.annotations.jakarta)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)

    implementation(libs.spring.doc.mvc)
}

tasks.test {
    useJUnitPlatform()
}