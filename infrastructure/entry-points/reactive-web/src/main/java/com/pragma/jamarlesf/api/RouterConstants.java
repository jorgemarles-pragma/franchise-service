package com.pragma.jamarlesf.api;

public final class RouterConstants {
    
    // Route Paths
    public static final String FRANCHISES_PATH = "/api/franchises";
    public static final String FRANCHISE_NAME_PATH = "/api/franchises/{franchiseId}/name";
    public static final String BRANCHES_PATH = "/api/franchises/{franchiseId}/branches";
    public static final String BRANCH_NAME_PATH = "/api/branches/{branchId}/name";
    public static final String PRODUCTS_PATH = "/api/branches/{branchId}/products";
    public static final String BRANCH_PRODUCT_PATH = "/api/branches/{branchId}/products/{productId}";
    public static final String PRODUCT_STOCK_PATH = "/api/products/{productId}/stock";
    public static final String PRODUCT_NAME_PATH = "/api/products/{productId}/name";
    public static final String FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH = "/api/franchises/{franchiseId}/max-stock-products";

    // Path Variables
    public static final String PATH_VAR_FRANCHISE_ID = "franchiseId";
    public static final String PATH_VAR_BRANCH_ID = "branchId";
    public static final String PATH_VAR_PRODUCT_ID = "productId";

    // Swagger Tags
    public static final String TAG_FRANCHISES = "Franchises";
    public static final String TAG_BRANCHES = "Branches";
    public static final String TAG_PRODUCTS = "Products";

    // Media Types
    public static final String APPLICATION_JSON = "application/json";

    private RouterConstants() {
        // Private constructor to prevent instantiation
    }
}
