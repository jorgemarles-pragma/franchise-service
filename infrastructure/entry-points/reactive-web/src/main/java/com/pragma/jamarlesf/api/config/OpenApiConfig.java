package com.pragma.jamarlesf.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franchise Management Reactive API (Reto Nequi)")
                        .version("1.0.0")
                        .description("Non-blocking Reactive REST API for franchise, branch, and product management " +
                                "built with Spring WebFlux Functional, R2DBC PostgreSQL, and Resilience4j.")
                        .contact(new Contact()
                                .name("Jorge Marles")
                                .email("jorge.marles@pragma.com.co"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
