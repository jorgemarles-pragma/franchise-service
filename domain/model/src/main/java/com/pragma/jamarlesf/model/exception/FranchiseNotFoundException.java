package com.pragma.jamarlesf.model.exception;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException(String id) {
        super(String.format(ErrorMessageConstants.FRANCHISE_NOT_FOUND_TEMPLATE, id));
    }
}
