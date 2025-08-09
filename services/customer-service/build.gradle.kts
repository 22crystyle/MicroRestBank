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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":shared"))
    implementation("org.keycloak:keycloak-admin-client:26.0.6")
    implementation("org.keycloak:keycloak-services:26.3.2")
    implementation("org.springframework.kafka:spring-kafka:3.3.8")
}

tasks.test {
    useJUnitPlatform()
}