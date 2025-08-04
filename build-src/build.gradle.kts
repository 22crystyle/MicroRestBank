plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    implementation(gradleApi())
}