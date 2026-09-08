package com.pragma.jamarlesf.api.constant;

public final class ApiTestConstants {

    // Common IDs
    public static final String ID_ONE = "1";
    public static final String ID_TWO = "2";
    public static final String ID_TEN = "10";
    public static final String ID_TWENTY = "20";
    public static final String ID_ONE_HUNDRED = "100";
    public static final String ID_NON_EXISTENT = "999";

    // Names
    public static final String FRANCHISE_NAME_DEFAULT = "McDonalds";
    public static final String FRANCHISE_NAME_UPDATED = "McDonalds Colombia";
    public static final String BRANCH_NAME_DEFAULT = "North Branch";
    public static final String BRANCH_NAME_UPDATED = "South Branch";
    public static final String PRODUCT_NAME_DEFAULT = "Double Burger";
    public static final String PRODUCT_NAME_UPDATED = "Triple Burger";
    public static final String PRODUCT_NAME_FRIES = "Medium Fries";

    // Numbers
    public static final int STOCK_FIFTY = 50;
    public static final int STOCK_SEVENTY_FIVE = 75;
    public static final int STOCK_EIGHTY = 80;

    // Messages
    public static final String SAMPLE_ERROR_MESSAGE = "Something went wrong";
    public static final String TEST_PATH = "/api/test";
    public static final String INVALID_NAME_MESSAGE = "Name cannot be empty";
    public static final String INVALID_STOCK_MESSAGE = "Stock must be positive";

    private ApiTestConstants() {
        // Private constructor to prevent instantiation
    }
}
