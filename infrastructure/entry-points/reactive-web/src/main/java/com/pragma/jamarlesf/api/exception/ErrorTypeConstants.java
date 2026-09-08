package com.pragma.jamarlesf.api.exception;

public final class ErrorTypeConstants {

    public static final String FRANCHISE_NOT_FOUND = "FRANCHISE_NOT_FOUND";
    public static final String BRANCH_NOT_FOUND = "BRANCH_NOT_FOUND";
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String MALFORMED_REQUEST = "MALFORMED_REQUEST";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";

    private ErrorTypeConstants() {
        // Private constructor to prevent instantiation
    }
}
