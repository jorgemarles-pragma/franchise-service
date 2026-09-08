package com.pragma.jamarlesf.model.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String id) {
        super(String.format("Product with id %s not found", id));
    }
}
