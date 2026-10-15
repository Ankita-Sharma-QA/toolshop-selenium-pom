package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.data.TestData;
import com.toolshop.pages.CartPage;
import com.toolshop.pages.CheckoutPage;
import com.toolshop.pages.ProductPage;
import com.toolshop.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutTests extends BaseTest {

    @Test(groups = {"smoke", "regression"},
          description = "Logged-in user buys a product with cash on delivery (end-to-end)")
    public void loggedInUserCanPlaceOrder() {
        // 1. Log in (with a separate user, so parallel login tests don't end this session)
        loginAs(ConfigReader.get("checkoutUserEmail"), ConfigReader.get("checkoutUserPassword"));

        // 2. Add a product to the cart
        ProductPage productPage = homePage.open()
                .searchFor(TestData.PRODUCT_HAMMER)
                .openProduct(TestData.PRODUCT_HAMMER);
        productPage.addToCart();
        header.waitForCartCount(1);

        // 3. Cart -> proceed
        CartPage cartPage = header.goToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        // 4. Sign-in step (already logged in)
        checkoutPage.continueAsLoggedInUser();

        // 5. Billing address: the site fills in street and city from the postcode
        checkoutPage.fillBillingAddress(TestData.COUNTRY_CODE, TestData.POSTAL_CODE, TestData.HOUSE_NUMBER);
        Assert.assertFalse(checkoutPage.getCity().isEmpty(), "City should be filled in from the postcode");
        Assert.assertFalse(checkoutPage.getStreet().isEmpty(), "Street should be filled in from the postcode");
        checkoutPage.proceedToPayment();

        // 6. Payment
        checkoutPage.selectPaymentMethod(TestData.PAYMENT_CASH_ON_DELIVERY);
        Assert.assertEquals(checkoutPage.confirmPayment(), "Payment was successful");

        // 7. Order confirmation
        String confirmation = checkoutPage.placeOrder();
        Assert.assertTrue(confirmation.startsWith("Thanks for your order!"),
                "Unexpected confirmation text: " + confirmation);
        Assert.assertTrue(checkoutPage.getInvoiceNumber().startsWith("INV-"),
                "Invoice number should start with INV-");
    }
}
