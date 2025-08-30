package com.example.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
            name = "Kryshtal Maxim",
            email = "shimorowm@gmail.com",
            url = "https://github.com/22crystyle"
        ),
        description = "OpenApi documentation for RestBank",
        title = "OpenApi specification - RestBank",
        version = "1.0",
        license = @License(
            name = "MIT",
            url = "https//opensource.org/licenses/MIT"
        ),
        termsOfService = "Terms of service" 
    )
)
@SecurityScheme(
    name = "BearerAuth",
    description = "JWT auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class BaseOpenApiConfig {
}
