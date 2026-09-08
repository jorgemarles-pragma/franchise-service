package com.pragma.jamarlesf.r2dbc.constant;

public final class R2dbcTestConstants {

    // Common IDs (Long & String)
    public static final Long ID_ONE_LONG = 1L;
    public static final Long ID_TWO_LONG = 2L;
    public static final Long ID_TEN_LONG = 10L;
    public static final Long ID_TWENTY_LONG = 20L;
    public static final Long ID_ONE_HUNDRED_LONG = 100L;
    public static final Long ID_ONE_HUNDRED_ONE_LONG = 101L;
    public static final Long ID_TWO_HUNDRED_LONG = 200L;
    public static final Long ID_TWO_HUNDRED_ONE_LONG = 201L;
    public static final Long ID_NINE_NINE_NINE_LONG = 999L;

    public static final String ID_ONE = "1";
    public static final String ID_TEN = "10";
    public static final String ID_TWENTY = "20";
    public static final String ID_ONE_HUNDRED = "100";
    public static final String ID_ONE_HUNDRED_ONE = "101";
    public static final String ID_TWO_HUNDRED_ONE = "201";
    public static final String ID_NINE_NINE_NINE = "999";
    public static final String ID_INVALID = "invalid-id";
    public static final String ID_NON_NUMERIC = "xyz";

    // Names
    public static final String FRANCHISE_NAME_DEFAULT = "Main Franchise";
    public static final String FRANCHISE_NAME_UPDATED = "Main Franchise Updated";
    public static final String BRANCH_NAME_DEFAULT = "Downtown Branch";
    public static final String BRANCH_NAME_UPDATED = "Downtown Branch Updated";
    public static final String PRODUCT_NAME_DEFAULT = "Special Burger";
    public static final String PRODUCT_NAME_UPDATED = "Special Burger Updated";
    public static final String PRODUCT_NAME_FRIES = "Supreme Fries";

    // Stock
    public static final int STOCK_FIVE = 5;
    public static final int STOCK_FIFTY = 50;
    public static final int STOCK_SEVENTY_FIVE = 75;
    public static final int STOCK_EIGHTY = 80;

    // Blank / Whitespace
    public static final String WHITESPACE_STRING = "   ";

    // Error messages
    public static final String ERROR_DB_TIMEOUT = "Database connection timeout";
    public static final String ERROR_DB_CONNECTION = "Database connection error";
    public static final String ERROR_DB_QUERY_FAILED = "DB query failed";
    public static final String ERROR_DB_DELETE_FAILED = "DB delete failed";
    public static final String ERROR_DB_CONNECTION_LOST = "DB connection lost";
    public static final String ERROR_FATAL_DB = "Fatal DB error";

    // Database Connection Pool Properties
    public static final String DB_HOST = "localhost";
    public static final int DB_PORT = 5432;
    public static final String DB_DATABASE = "dbName";
    public static final String DB_SCHEMA = "schema";
    public static final String DB_USERNAME = "username";
    public static final String DB_PASSWORD = "password";

    // Dummy / Generic Test Data
    public static final String ID_TWO = "2";
    public static final String TEST_NAME = "test";
    public static final String TEST_NAME_ONE = "test1";
    public static final String TEST_NAME_TWO = "test2";

    // Resilience Test Constants
    public static final String RESILIENCE_CB_NAME = "testCB";
    public static final String RESILIENCE_RETRY_NAME = "testRetry";
    public static final String RESILIENCE_TL_NAME = "testTL";
    public static final String TEST_DATA = "data";
    public static final String TEST_ITEM_1 = "item1";
    public static final String TEST_ITEM_2 = "item2";
    public static final String TEST_RECOVERED = "recovered";
    public static final String TEST_LATE = "late";
    public static final String TEST_WONT_EXECUTE = "wont-execute";

    private R2dbcTestConstants() {
        // Private constructor to prevent instantiation
    }
}
