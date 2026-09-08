package com.pragma.jamarlesf.model.exception;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;

public class InvalidProductNameException extends RuntimeException {

    public InvalidProductNameException() {
        super(ErrorMessageConstants.PRODUCT_NAME_CANNOT_BE_EMPTY_OR_NULL);
    }

    public InvalidProductNameException(String message) {
        super(message);
    }
}
