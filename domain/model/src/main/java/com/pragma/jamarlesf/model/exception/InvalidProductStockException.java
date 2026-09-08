package com.pragma.jamarlesf.model.exception;

public class InvalidProductStockException extends RuntimeException {
    public InvalidProductStockException(String message) {
        super(message);
    }
}
