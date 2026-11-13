package com.irfan.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "QR-code-attendance-System",
                version = "1.0.0",
                description = "API documentation for the Foodie Application",
                termsOfService = "http://example.com/terms"
        )
)
public class SwaggerConfig {
}