dependencies {
    implementation(project(":shared"))
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
    apiDocsUrl.set("http://localhost:1024/api/v1/cards/v3/api-docs")
}