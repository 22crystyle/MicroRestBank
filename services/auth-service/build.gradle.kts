dependencies {
    implementation(project(":shared"))
    implementation(libs.keycloak.admin.client)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.spring.doc.flux)
}

openApi {
    val authServicePort = System.getenv("AUTH_SERVICE_PORT") ?: "1025"
    apiDocsUrl.set("http://localhost:$authServicePort/v3/api-docs")
}