package com.pragma.jamarlesf.model.exception;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;

public class InvalidFranchiseNameException extends RuntimeException {

    public InvalidFranchiseNameException() {
        super(ErrorMessageConstants.FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL);
    }

    public InvalidFranchiseNameException(String message) {
        super(message);
    }
}
