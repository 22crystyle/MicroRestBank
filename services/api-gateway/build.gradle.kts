dependencies {
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.spring.cloud.starter.circuitbreaker.reactor.resilience4j)
    implementation(libs.spring.cloud.starter.loadbalancer)
    implementation(libs.spring.doc.flux)
    testImplementation(libs.spring.cloud.starter.contract.stub.runner)
    testImplementation(libs.reactor.test)

}

openApi {
    apiDocsUrl.set("http://localhost:1024/v3/api-docs")
    groupedApiMappings.set(
        mapOf(
            "http://localhost:1024/api/v1/auth/v3/api-docs" to "auth-service.json",
            "http://localhost:1024/api/v1/customers/v3/api-docs" to "customer-service.json",
            "http://localhost:1024/api/v1/cards/v3/api-docs" to "card-service.json"
        )
    )
}