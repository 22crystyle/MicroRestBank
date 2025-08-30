dependencies {
    implementation(project(":shared"))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.keycloak.admin.client)
    implementation(libs.keycloak.services)
    implementation(libs.spring.kafka)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.lombok.mapstruct.binding)

    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)

    implementation(libs.spring.doc.mvc)
}

openApi {
    apiDocsUrl.set("http://localhost:1024/customers/v3/api-docs")
}