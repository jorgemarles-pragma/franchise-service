package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidBranchNameException;
import com.pragma.jamarlesf.model.exception.InvalidFranchiseNameException;
import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FranchiseNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleFranchiseNotFoundException(FranchiseNotFoundException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", ex.getMessage()))
        );
    }

    @ExceptionHandler(BranchNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleBranchNotFoundException(BranchNotFoundException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", ex.getMessage()))
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleProductNotFoundException(ProductNotFoundException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", ex.getMessage()))
        );
    }

    @ExceptionHandler(InvalidProductStockException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleInvalidProductStockException(InvalidProductStockException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", ex.getMessage()))
        );
    }

    @ExceptionHandler(InvalidFranchiseNameException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleInvalidFranchiseNameException(InvalidFranchiseNameException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", ex.getMessage()))
        );
    }

    @ExceptionHandler(InvalidBranchNameException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleInvalidBranchNameException(InvalidBranchNameException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", ex.getMessage()))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGeneralException(Exception ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Internal server error"))
        );
    }
}
