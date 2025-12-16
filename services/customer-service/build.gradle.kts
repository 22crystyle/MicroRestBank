dependencies {
    implementation(project(":libs:api-contract"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.hateoas)
    implementation(libs.spring.kafka)
    implementation(libs.spring.doc.mvc)
    implementation(libs.keycloak.admin.client)
    implementation(libs.keycloak.services)
    implementation(libs.liquibase.core)
    implementation(libs.lombok.mapstruct.binding)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)

}

openApi {
    val customerServicePort = System.getenv("CUSTOMER_SERVICE_PORT") ?: "1027"
    apiDocsUrl.set("http://localhost:$customerServicePort/v3/api-docs")
}