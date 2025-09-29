dependencies {
    implementation(project(":shared"))
    implementation(libs.keycloak.admin.client)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.spring.doc.flux)
}

openApi {
    apiDocsUrl.set("http://localhost:1024/api/v1/auth/v3/api-docs")
}