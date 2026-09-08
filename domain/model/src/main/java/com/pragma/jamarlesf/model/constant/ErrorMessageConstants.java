package com.pragma.jamarlesf.model.constant;

public final class ErrorMessageConstants {

    public static final String FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL = "Franchise name cannot be empty or null";
    public static final String BRANCH_NAME_CANNOT_BE_EMPTY_OR_NULL = "Branch name cannot be empty or null";
    public static final String PRODUCT_NAME_CANNOT_BE_EMPTY_OR_NULL = "Product name cannot be empty or null";
    public static final String PRODUCT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO = "Product stock must be greater than or equal to 0";
    public static final String PRODUCT_NOT_BELONG_TO_BRANCH = "Product does not belong to the specified branch";
    public static final String FRANCHISE_NOT_FOUND_TEMPLATE = "Franchise with id %s not found";
    public static final String BRANCH_NOT_FOUND_TEMPLATE = "Branch with id %s not found";
    public static final String PRODUCT_NOT_FOUND_TEMPLATE = "Product with id %s not found";
    public static final String NULL_ID_VALUE = "null";

    private ErrorMessageConstants() {
        // Private constructor to prevent instantiation
    }
}
