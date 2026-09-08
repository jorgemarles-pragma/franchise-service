package com.pragma.jamarlesf.api;

public final class RouterConstants {
    
    public static final String FRANCHISES_PATH = "/api/franchises";
    public static final String FRANCHISE_NAME_PATH = "/api/franchises/{franchiseId}/name";
    public static final String BRANCHES_PATH = "/api/franchises/{franchiseId}/branches";
    public static final String BRANCH_NAME_PATH = "/api/branches/{branchId}/name";
    public static final String PRODUCTS_PATH = "/api/branches/{branchId}/products";
    public static final String BRANCH_PRODUCT_PATH = "/api/branches/{branchId}/products/{productId}";
    public static final String PRODUCT_STOCK_PATH = "/api/products/{productId}/stock";
    public static final String PRODUCT_NAME_PATH = "/api/products/{productId}/name";
    public static final String FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH = "/api/franchises/{franchiseId}/max-stock-products";
    
    private RouterConstants() {
        // Private constructor to prevent instantiation
    }
}
