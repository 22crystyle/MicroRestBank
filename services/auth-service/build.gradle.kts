plugins {
    id("java")
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

openApi {
    apiDocsUrl.set("http://localhost:1024/v3/api-docs.yaml")
    outputDir.set(layout.projectDirectory.dir("docs"))
    outputFileName.set("openapi.yaml")
}