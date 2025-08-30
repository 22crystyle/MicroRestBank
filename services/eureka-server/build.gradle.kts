// The java-convention plugin is applied via the root build.gradle.kts.
// We exclude dependencies that are not needed by the Eureka server.
configurations.implementation {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-validation")
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-oauth2-resource-server")
    exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    exclude(group = "org.springdoc", module = "springdoc-openapi-starter-webmvc-api")
    exclude(group = "org.springframework.cloud", module = "spring-cloud-starter-netflix-eureka-client")
}

dependencies {
    implementation(libs.spring.cloud.starter.eureka.server)
    implementation(libs.spring.boot.starter.security)
}
