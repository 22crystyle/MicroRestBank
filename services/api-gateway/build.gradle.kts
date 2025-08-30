dependencies {
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.spring.cloud.starter.circuitbreaker.reactor.resilience4j)
    implementation(libs.spring.cloud.starter.loadbalancer)
    testImplementation(libs.spring.cloud.starter.contract.stub.runner)
    testImplementation(libs.reactor.test)

    implementation(libs.spring.doc.flux)
}

openApi {
    apiDocsUrl.set("http://localhost:1024/v3/api-docs")
    groupedApiMappings.set(
        mapOf(
            "http://localhost:1024/auth/v3/api-docs" to "auth-service.json",
            "http://localhost:1024/customers/v3/api-docs" to "customer-service.json",
            "http://localhost:1024/cards/v3/api-docs" to "card-service.json"
        )
    )
    customBootRun {
        environment.put("API_GATEWAY_PORT", "7080")
        args.set(listOf("--spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:7080/realms/bank-realm"))
    }
}