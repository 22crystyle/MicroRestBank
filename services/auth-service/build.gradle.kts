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
    implementation(project(":shared"))
    implementation(libs.keycloak.admin.client)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.security)
}

tasks.test {
    useJUnitPlatform()
}

openApi {
    apiDocsUrl.set("http://localhost:1024/v3/api-docs.yaml")
    outputDir.set(layout.projectDirectory.dir("docs"))
    outputFileName.set("openapi.yaml")
}