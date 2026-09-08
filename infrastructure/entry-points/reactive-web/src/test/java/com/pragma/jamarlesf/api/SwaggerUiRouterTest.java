package com.pragma.jamarlesf.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

class SwaggerUiRouterTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        SwaggerUiRouter swaggerUiRouter = new SwaggerUiRouter();
        webTestClient = WebTestClient.bindToRouterFunction(swaggerUiRouter.swaggerRoutes()).build();
    }

    @Test
    @DisplayName("Should serve swagger-initializer.js configured with /v3/api-docs")
    void shouldServeCustomSwaggerInitializerJs() {
        webTestClient.get()
                .uri("/webjars/swagger-ui/swagger-initializer.js")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/javascript;charset=UTF-8")
                .expectBody(String.class)
                .value(body -> org.junit.jupiter.api.Assertions.assertTrue(body.contains("url: \"/v3/api-docs\"")));
    }

    @Test
    @DisplayName("Should serve swagger-ui.html page")
    void shouldServeSwaggerUiHtml() {
        webTestClient.get()
                .uri("/swagger-ui.html")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .value(body -> org.junit.jupiter.api.Assertions.assertTrue(body.contains("<div id=\"swagger-ui\"></div>")));
    }

    @Test
    @DisplayName("Should serve swagger-ui/index.html page")
    void shouldServeSwaggerUiIndexHtml() {
        webTestClient.get()
                .uri("/swagger-ui/index.html")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .value(body -> org.junit.jupiter.api.Assertions.assertTrue(body.contains("<div id=\"swagger-ui\"></div>")));
    }

    @Test
    @DisplayName("Should redirect /swagger-ui to /swagger-ui/index.html")
    void shouldRedirectSwaggerUi() {
        webTestClient.get()
                .uri("/swagger-ui")
                .exchange()
                .expectStatus().isTemporaryRedirect()
                .expectHeader().location("/swagger-ui/index.html");
    }

    @Test
    @DisplayName("Should redirect /docs to /swagger-ui/index.html")
    void shouldRedirectDocs() {
        webTestClient.get()
                .uri("/docs")
                .exchange()
                .expectStatus().isTemporaryRedirect()
                .expectHeader().location("/swagger-ui/index.html");
    }
}
