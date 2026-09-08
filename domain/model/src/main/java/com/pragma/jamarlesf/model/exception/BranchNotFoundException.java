package com.pragma.jamarlesf.model.exception;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String id) {
        super(String.format(ErrorMessageConstants.BRANCH_NOT_FOUND_TEMPLATE, id));
    }
}
