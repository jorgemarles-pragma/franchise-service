package com.pragma.jamarlesf.api.exception;

import tools.jackson.databind.ObjectMapper;
import com.pragma.jamarlesf.api.dto.response.ErrorResponse;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidBranchNameException;
import com.pragma.jamarlesf.model.exception.InvalidFranchiseNameException;
import com.pragma.jamarlesf.model.exception.InvalidProductNameException;
import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalErrorWebExceptionHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private GlobalErrorWebExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalErrorWebExceptionHandler(objectMapper);
    }

    @Test
    @DisplayName("Should return 404 when FranchiseNotFoundException is thrown")
    void shouldReturn404WhenFranchiseNotFoundExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/franchises/999"));
        FranchiseNotFoundException ex = new FranchiseNotFoundException("999");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(404, response.status());
                        assertEquals("FRANCHISE_NOT_FOUND", response.error());
                        assertTrue(response.message().contains("999"));
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 404 when BranchNotFoundException is thrown")
    void shouldReturn404WhenBranchNotFoundExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/branches/888"));
        BranchNotFoundException ex = new BranchNotFoundException("888");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(404, response.status());
                        assertEquals("BRANCH_NOT_FOUND", response.error());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 404 when ProductNotFoundException is thrown")
    void shouldReturn404WhenProductNotFoundExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/products/777"));
        ProductNotFoundException ex = new ProductNotFoundException("777");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(404, response.status());
                        assertEquals("PRODUCT_NOT_FOUND", response.error());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidFranchiseNameException is thrown")
    void shouldReturn400WhenInvalidFranchiseNameExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/franchises"));
        InvalidFranchiseNameException ex = new InvalidFranchiseNameException("Franchise name cannot be empty or null");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(400, response.status());
                        assertEquals("VALIDATION_ERROR", response.error());
                        assertEquals("Franchise name cannot be empty or null", response.message());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidBranchNameException is thrown")
    void shouldReturn400WhenInvalidBranchNameExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.patch("/api/branches/1/name"));
        InvalidBranchNameException ex = new InvalidBranchNameException("Branch name cannot be empty or null");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(400, response.status());
                        assertEquals("VALIDATION_ERROR", response.error());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidProductNameException is thrown")
    void shouldReturn400WhenInvalidProductNameExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.patch("/api/products/1/name"));
        InvalidProductNameException ex = new InvalidProductNameException("Product name cannot be empty or null");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(400, response.status());
                        assertEquals("VALIDATION_ERROR", response.error());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidProductStockException is thrown")
    void shouldReturn400WhenInvalidProductStockExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.patch("/api/products/1/stock"));
        InvalidProductStockException ex = new InvalidProductStockException("Product stock must be greater than or equal to 0");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(400, response.status());
                        assertEquals("VALIDATION_ERROR", response.error());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when ServerWebInputException is thrown")
    void shouldReturn400WhenServerWebInputExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/franchises"));
        ServerWebInputException ex = new ServerWebInputException("Failed to read HTTP message");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(400, response.status());
                        assertEquals("MALFORMED_REQUEST", response.error());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when DecodingException is thrown")
    void shouldReturn400WhenDecodingExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/franchises"));
        DecodingException ex = new DecodingException("JSON parse error");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(400, response.status());
                        assertEquals("MALFORMED_REQUEST", response.error());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 500 when unexpected Exception is thrown")
    void shouldReturn500WhenUnexpectedExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/franchises"));
        RuntimeException ex = new RuntimeException("Unexpected database failure");

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(500, response.status());
                        assertEquals("INTERNAL_SERVER_ERROR", response.error());
                        assertEquals("Unexpected database failure", response.message());
                    } catch (Exception e) {
                        throw new AssertionError("Failed to deserialize ErrorResponse", e);
                    }
                })
                .verifyComplete();
    }
}
