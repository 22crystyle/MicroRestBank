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

    implementation(libs.spring.doc.mvc)
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("forkedSpringBootRun") {
    dependsOn(project(":shared").tasks.getByName("jar"))
}

openApi {
    apiDocsUrl.set("http://localhost:1024/customers/v3/api-docs")
    outputDir.set(file("$projectDir/docs"))
    outputFileName.set("swagger.json")
    customBootRun {
        args.set(listOf("--spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:7080/realms/bank-realm"))
    }
}