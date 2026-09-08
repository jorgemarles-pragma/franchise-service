package com.pragma.jamarlesf.model.exception;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException(String id) {
        super(String.format("Franchise with id %s not found", id));
    }
}
