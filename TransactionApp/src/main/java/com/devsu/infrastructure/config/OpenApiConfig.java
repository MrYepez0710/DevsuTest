package com.devsu.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration for TransactionApp
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI transactionAppOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("TransactionApp API")
                .description("API REST para gestión de cuentas, movimientos y reportes - Microservicio de Transacciones")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Devsu")
                    .email("info@devsu.com")
                    .url("https://devsu.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8081/api")
                    .description("Servidor Local"),
                new Server()
                    .url("http://transactionapp:8081/api")
                    .description("Servidor Docker")
            ));
    }
}
