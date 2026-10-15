package com.toolshop.data;

/**
 * Test data used across tests, kept in one place
 * so a change (e.g. a renamed product) is made only once.
 */
public class TestData {

    // Products (in stock on the demo site)
    public static final String PRODUCT_PLIERS = "Combination Pliers";
    public static final String PRODUCT_HAMMER = "Claw Hammer";

    // Search
    public static final String SEARCH_TERM = "Pliers";
    public static final String SEARCH_NO_RESULTS = "xyz-no-such-product";

    // Category filter
    public static final String CATEGORY_HAMMER = "Hammer";

    // Login
    public static final String WRONG_PASSWORD = "wrongPassword123";

    // Billing address (street, city and state are filled in by the site from the postcode)
    public static final String COUNTRY_CODE = "IN";
    public static final String POSTAL_CODE = "201301";
    public static final String HOUSE_NUMBER = "42";

    // Payment
    public static final String PAYMENT_CASH_ON_DELIVERY = "cash-on-delivery";
}
