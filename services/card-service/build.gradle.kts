dependencies {
    implementation(project(":libs:api-contract"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.hateoas)

    implementation(libs.spring.cloud.openfeign)
    implementation(libs.spring.retry)
    implementation(libs.spring.kafka)
    implementation(libs.spring.doc.mvc)

    implementation(libs.resilience4j.spring.boot2)
    implementation(libs.liquibase.core)

    implementation(libs.mapstruct)
    implementation(libs.lombok.mapstruct.binding)

    annotationProcessor(libs.mapstruct.processor)
    runtimeOnly(libs.resilience4j.feign)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)

}

openApi {
    val cardServicePort = System.getenv("CARD_SERVICE_PORT") ?: "1026"
    apiDocsUrl.set("http://localhost:$cardServicePort/v3/api-docs")
}