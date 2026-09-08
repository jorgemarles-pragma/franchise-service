package com.pragma.jamarlesf.model.exception;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String id) {
        super(ErrorMessageConstants.PRODUCT_NOT_BELONG_TO_BRANCH.equals(id)
                ? id
                : String.format(ErrorMessageConstants.PRODUCT_NOT_FOUND_TEMPLATE, id));
    }
}
