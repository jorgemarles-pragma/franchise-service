package com.pragma.jamarlesf.model.exception;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String id) {
        super(String.format("Branch with id %s not found", id));
    }
}
