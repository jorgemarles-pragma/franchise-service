package com.pragma.jamarlesf.api;

/**
 * Constant definitions for REST API endpoint route patterns, path variables, tags, and MIME types.
 * java:S1075 is suppressed because these strings are declarative route path patterns for Spring WebFlux
 * RouterFunction mappings, not external remote URIs or file paths.
 */
@SuppressWarnings("java:S1075")
public final class RouterConstants {
    
    // Base Route Paths
    public static final String ROUTER_FRANCHISES = "/api/franchises";
    public static final String ROUTER_BRANCHES = "/api/branches";
    public static final String ROUTER_PRODUCTS = "/api/products";

    // Path Variables
    public static final String PATH_VAR_FRANCHISE_ID = "franchiseId";
    public static final String PATH_VAR_BRANCH_ID = "branchId";
    public static final String PATH_VAR_PRODUCT_ID = "productId";

    // Route Paths
    public static final String FRANCHISES_PATH = ROUTER_FRANCHISES;
    public static final String FRANCHISE_NAME_PATH = ROUTER_FRANCHISES + "/{" + PATH_VAR_FRANCHISE_ID + "}/name";
    public static final String BRANCHES_PATH = ROUTER_FRANCHISES + "/{" + PATH_VAR_FRANCHISE_ID + "}/branches";
    public static final String BRANCH_NAME_PATH = ROUTER_BRANCHES + "/{" + PATH_VAR_BRANCH_ID + "}/name";
    public static final String PRODUCTS_PATH = ROUTER_BRANCHES + "/{" + PATH_VAR_BRANCH_ID + "}/products";
    public static final String BRANCH_PRODUCT_PATH = ROUTER_BRANCHES + "/{" + PATH_VAR_BRANCH_ID + "}/products/{" + PATH_VAR_PRODUCT_ID + "}";
    public static final String PRODUCT_STOCK_PATH = ROUTER_PRODUCTS + "/{" + PATH_VAR_PRODUCT_ID + "}/stock";
    public static final String PRODUCT_NAME_PATH = ROUTER_PRODUCTS + "/{" + PATH_VAR_PRODUCT_ID + "}/name";
    public static final String FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH = ROUTER_FRANCHISES + "/{" + PATH_VAR_FRANCHISE_ID + "}/max-stock-products";

    public static final String ROUTER_PRODUCTS_GET_BY_ID = ROUTER_PRODUCTS + "/{" + PATH_VAR_PRODUCT_ID + "}";
    public static final String ROUTER_BRANCHES_PRODUCTS = ROUTER_BRANCHES + "/{" + PATH_VAR_BRANCH_ID + "}/products";


    // Swagger Tags
    public static final String TAG_FRANCHISES = "Franchises";
    public static final String TAG_BRANCHES = "Branches";
    public static final String TAG_PRODUCTS = "Products";

    // Media Types
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_NDJSON = "application/x-ndjson";

    private RouterConstants() {
        // Private constructor to prevent instantiation
    }
}
