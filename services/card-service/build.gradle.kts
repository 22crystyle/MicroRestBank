dependencies {
    implementation(project(":libs:api-contract"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.retry)
    implementation(libs.liquibase.core)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.hateoas)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)

    implementation(libs.spring.cloud.openfeign)
    implementation(libs.resilience4j.spring.boot2)
    implementation(libs.spring.kafka)
    runtimeOnly(libs.resilience4j.feign)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)

    implementation(libs.spring.doc.mvc)
}

openApi {
    val cardServicePort = System.getenv("CARD_SERVICE_PORT") ?: "1026"
    apiDocsUrl.set("http://localhost:$cardServicePort/v3/api-docs")
}