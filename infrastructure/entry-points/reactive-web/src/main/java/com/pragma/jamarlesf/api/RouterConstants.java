package com.pragma.jamarlesf.api;

public final class RouterConstants {
    
    public static final String FRANCHISES_PATH = "/api/franchises";
    public static final String BRANCHES_PATH = "/api/franchises/{franchiseId}/branches";
    public static final String PRODUCTS_PATH = "/api/branches/{branchId}/products";
    
    private RouterConstants() {
        // Private constructor to prevent instantiation
    }
}
