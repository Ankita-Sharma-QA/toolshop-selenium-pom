package com.toolshop.pages;

import com.toolshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Checkout steps 2, 3 and 4 (step 1 is the cart, see CartPage).
 *   Step 2: Sign in      (already logged in -> just click "Proceed to checkout")
 *   Step 3: Billing address
 *   Step 4: Payment
 */
public class CheckoutPage extends BasePage {

    // Step 2 - sign in
    private final By proceedFromSignInButton = byDataTest("proceed-2");

    // Step 3 - billing address
    private final By countryDropdown = byDataTest("country");
    private final By postalCodeInput = byDataTest("postal_code");
    private final By houseNumberInput = byDataTest("house_number");
    private final By streetInput = byDataTest("street");
    private final By cityInput = byDataTest("city");
    private final By stateInput = byDataTest("state");
    private final By proceedFromAddressButton = byDataTest("proceed-3");

    // Step 4 - payment
    private final By paymentMethodDropdown = byDataTest("payment-method");
    private final By finishButton = byDataTest("finish");
    private final By paymentSuccessMessage = byDataTest("payment-success-message");
    private final By orderConfirmation = By.id("order-confirmation");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void continueAsLoggedInUser() {
        click(proceedFromSignInButton);
    }

    /**
     * Fills the billing address the way a real user does:
     * enter country, postcode and house number, and the site looks up
     * street, city and state automatically. (The server also checks that
     * city and state match the postcode, so we must not type our own.)
     */
    public void fillBillingAddress(String countryCode, String postalCode, String houseNumber) {
        // Empty the fields first (the user's saved address is pre-filled),
        // so we can see when the lookup has filled them in.
        clearField(streetInput);
        clearField(cityInput);
        clearField(stateInput);

        selectByValue(countryDropdown, countryCode);
        type(postalCodeInput, postalCode);
        type(houseNumberInput, houseNumber);

        waitForValueNotEmpty(cityInput);
        waitForValueNotEmpty(streetInput);
    }

    public String getCity() {
        return getValue(cityInput);
    }

    public String getStreet() {
        return getValue(streetInput);
    }

    public void proceedToPayment() {
        click(proceedFromAddressButton);
    }

    /** Payment methods: "bank-transfer", "cash-on-delivery", "credit-card", "buy-now-pay-later", "gift-card" */
    public void selectPaymentMethod(String method) {
        selectByValue(paymentMethodDropdown, method);
    }

    /** First click on the green button checks the payment. Returns "Payment was successful". */
    public String confirmPayment() {
        click(finishButton);
        return getText(paymentSuccessMessage);
    }

    /** Second click places the order. Returns the thank-you text with the invoice number. */
    public String placeOrder() {
        click(finishButton);
        // Creating the order and invoice can take a while on the server, so wait longer here
        return waitForVisible(orderConfirmation, 40).getText().trim();
    }

    /**
     * Reads the invoice number from the thank-you text,
     * e.g. "Thanks for your order! Your invoice number is INV-2026000123." -> "INV-2026000123"
     */
    public String getInvoiceNumber() {
        String text = getText(orderConfirmation);
        if (!text.contains("INV-")) {
            return "";
        }
        String afterLabel = text.substring(text.indexOf("INV-"));
        return afterLabel.replace(".", "").trim();
    }
}
