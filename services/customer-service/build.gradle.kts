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
}

tasks.test {
    useJUnitPlatform()
}