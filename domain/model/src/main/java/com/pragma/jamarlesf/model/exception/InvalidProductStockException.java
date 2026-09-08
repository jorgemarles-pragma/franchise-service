package com.pragma.jamarlesf.model.exception;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;

public class InvalidProductStockException extends RuntimeException {

    public InvalidProductStockException() {
        super(ErrorMessageConstants.PRODUCT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO);
    }

    public InvalidProductStockException(String message) {
        super(message);
    }
}
