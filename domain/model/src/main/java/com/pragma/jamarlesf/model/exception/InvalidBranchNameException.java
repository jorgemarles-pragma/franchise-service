package com.pragma.jamarlesf.model.exception;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;

public class InvalidBranchNameException extends RuntimeException {

    public InvalidBranchNameException() {
        super(ErrorMessageConstants.BRANCH_NAME_CANNOT_BE_EMPTY_OR_NULL);
    }

    public InvalidBranchNameException(String message) {
        super(message);
    }
}
