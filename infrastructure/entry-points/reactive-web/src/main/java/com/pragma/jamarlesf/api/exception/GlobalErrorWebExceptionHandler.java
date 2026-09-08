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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return Mono.just(exchange.getResponse().isCommitted())
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.defer(() -> Mono.error(ex)))
                .flatMap(notCommitted -> writeErrorResponse(exchange, ex));
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, Throwable ex) {
        HttpStatusCode status = resolveHttpStatus(ex);
        ErrorResponse errorBody = ErrorResponse.builder()
                .status(status.value())
                .error(resolveErrorType(ex))
                .message(ex.getMessage() != null ? ex.getMessage() : "Unexpected error occurred")
                .timestamp(Instant.now().toString())
                .build();

        log.error("Handling reactive exception [{}]: {}", status.value(), ex.getMessage());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(errorBody))
                .flatMap(bytes -> {
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    return exchange.getResponse().writeWith(Mono.just(buffer));
                });
    }

    private HttpStatusCode resolveHttpStatus(Throwable ex) {
        if (ex instanceof FranchiseNotFoundException
                || ex instanceof BranchNotFoundException
                || ex instanceof ProductNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof InvalidFranchiseNameException
                || ex instanceof InvalidBranchNameException
                || ex instanceof InvalidProductNameException
                || ex instanceof InvalidProductStockException
                || ex instanceof ServerWebInputException
                || ex instanceof DecodingException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveErrorType(Throwable ex) {
        if (ex instanceof FranchiseNotFoundException) {
            return "FRANCHISE_NOT_FOUND";
        }
        if (ex instanceof BranchNotFoundException) {
            return "BRANCH_NOT_FOUND";
        }
        if (ex instanceof ProductNotFoundException) {
            return "PRODUCT_NOT_FOUND";
        }
        if (ex instanceof InvalidFranchiseNameException
                || ex instanceof InvalidBranchNameException
                || ex instanceof InvalidProductNameException
                || ex instanceof InvalidProductStockException) {
            return "VALIDATION_ERROR";
        }
        if (ex instanceof ServerWebInputException || ex instanceof DecodingException) {
            return "MALFORMED_REQUEST";
        }
        return "INTERNAL_SERVER_ERROR";
    }
}
