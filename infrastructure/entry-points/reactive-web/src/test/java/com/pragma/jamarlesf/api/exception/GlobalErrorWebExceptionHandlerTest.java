package com.pragma.jamarlesf.api.exception;

import tools.jackson.databind.ObjectMapper;
import com.pragma.jamarlesf.api.constant.ApiTestConstants;
import com.pragma.jamarlesf.api.dto.response.ErrorResponse;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
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
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(ApiTestConstants.TEST_PATH));
        FranchiseNotFoundException ex = new FranchiseNotFoundException(ApiTestConstants.ID_NON_EXISTENT);

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
                        assertEquals(ErrorTypeConstants.FRANCHISE_NOT_FOUND, response.error());
                        assertTrue(response.message().contains(ApiTestConstants.ID_NON_EXISTENT));
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 404 when BranchNotFoundException is thrown")
    void shouldReturn404WhenBranchNotFoundExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(ApiTestConstants.TEST_PATH));
        BranchNotFoundException ex = new BranchNotFoundException(ApiTestConstants.ID_NON_EXISTENT);

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
                        assertEquals(ErrorTypeConstants.BRANCH_NOT_FOUND, response.error());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 404 when ProductNotFoundException is thrown")
    void shouldReturn404WhenProductNotFoundExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(ApiTestConstants.TEST_PATH));
        ProductNotFoundException ex = new ProductNotFoundException(ApiTestConstants.ID_NON_EXISTENT);

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
                        assertEquals(ErrorTypeConstants.PRODUCT_NOT_FOUND, response.error());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidFranchiseNameException is thrown")
    void shouldReturn400WhenInvalidFranchiseNameExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post(ApiTestConstants.TEST_PATH));
        InvalidFranchiseNameException ex = new InvalidFranchiseNameException();

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
                        assertEquals(ErrorTypeConstants.VALIDATION_ERROR, response.error());
                        assertEquals(ErrorMessageConstants.FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL, response.message());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidBranchNameException is thrown")
    void shouldReturn400WhenInvalidBranchNameExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.patch(ApiTestConstants.TEST_PATH));
        InvalidBranchNameException ex = new InvalidBranchNameException();

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
                        assertEquals(ErrorTypeConstants.VALIDATION_ERROR, response.error());
                        assertEquals(ErrorMessageConstants.BRANCH_NAME_CANNOT_BE_EMPTY_OR_NULL, response.message());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidProductNameException is thrown")
    void shouldReturn400WhenInvalidProductNameExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.patch(ApiTestConstants.TEST_PATH));
        InvalidProductNameException ex = new InvalidProductNameException();

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
                        assertEquals(ErrorTypeConstants.VALIDATION_ERROR, response.error());
                        assertEquals(ErrorMessageConstants.PRODUCT_NAME_CANNOT_BE_EMPTY_OR_NULL, response.message());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when InvalidProductStockException is thrown")
    void shouldReturn400WhenInvalidProductStockExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.patch(ApiTestConstants.TEST_PATH));
        InvalidProductStockException ex = new InvalidProductStockException();

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
                        assertEquals(ErrorTypeConstants.VALIDATION_ERROR, response.error());
                        assertEquals(ErrorMessageConstants.PRODUCT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO, response.message());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when ServerWebInputException is thrown")
    void shouldReturn400WhenServerWebInputExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post(ApiTestConstants.TEST_PATH));
        ServerWebInputException ex = new ServerWebInputException(ApiTestConstants.INVALID_NAME_MESSAGE);

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
                        assertEquals(ErrorTypeConstants.MALFORMED_REQUEST, response.error());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 400 when DecodingException is thrown")
    void shouldReturn400WhenDecodingExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post(ApiTestConstants.TEST_PATH));
        DecodingException ex = new DecodingException(ApiTestConstants.INVALID_NAME_MESSAGE);

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
                        assertEquals(ErrorTypeConstants.MALFORMED_REQUEST, response.error());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return 500 when unexpected Exception is thrown")
    void shouldReturn500WhenUnexpectedExceptionIsThrown() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(ApiTestConstants.TEST_PATH));
        RuntimeException ex = new RuntimeException(ApiTestConstants.SAMPLE_ERROR_MESSAGE);

        StepVerifier.create(handler.handle(exchange, ex))
                .verifyComplete();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());

        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    try {
                        ErrorResponse response = objectMapper.readValue(body, ErrorResponse.class);
                        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
                        assertEquals(ErrorTypeConstants.INTERNAL_SERVER_ERROR, response.error());
                        assertEquals(ApiTestConstants.SAMPLE_ERROR_MESSAGE, response.message());
                    } catch (Exception e) {
                        throw new AssertionError(ApiTestConstants.SAMPLE_ERROR_MESSAGE, e);
                    }
                })
                .verifyComplete();
    }
}
