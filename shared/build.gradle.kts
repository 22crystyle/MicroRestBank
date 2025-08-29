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
    implementation(libs.spring.doc.mvc)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)
}

tasks.test {
    useJUnitPlatform()
}