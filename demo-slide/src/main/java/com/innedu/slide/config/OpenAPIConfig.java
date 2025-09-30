package com.innedu.slide.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
    @Info(
        title = "Orchid Service API",
        version = "1.0",
        contact = @Contact(name = "API Support", email = "support@orchid.com")),
    security = @SecurityRequirement(name = "bearer-jwt"),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Dev (HTTP)"),
        @Server(url = "https://api.example.com", description = "Production (HTTPS)")
    })
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(
                new io.swagger.v3.oas.models.info.Info()
                    .title("Orchid Service API")
                    .version("1.0")
                    .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
}