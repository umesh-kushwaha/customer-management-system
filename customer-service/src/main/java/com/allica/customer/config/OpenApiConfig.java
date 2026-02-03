package com.allica.customer.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Customer Service API",
                version = "1.0.0",
                description = "Backend API for the customer-ui application."
        )
)
public class OpenApiConfig {
}
