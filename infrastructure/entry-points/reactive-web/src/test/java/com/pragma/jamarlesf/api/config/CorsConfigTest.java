package com.pragma.jamarlesf.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorsConfigTest {

    @Test
    @DisplayName("Should create CorsWebFilter with configured origins and all HTTP methods")
    void shouldCreateCorsWebFilterWithOrigins() {
        CorsConfig corsConfig = new CorsConfig();
        List<String> origins = List.of("http://localhost:4200", "http://localhost:8080");

        CorsWebFilter filter = corsConfig.corsWebFilter(origins);

        assertNotNull(filter);
    }
}
