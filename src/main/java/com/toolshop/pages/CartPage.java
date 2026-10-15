package com.toolshop.pages;

import com.toolshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Locale;

/** Step 1 of checkout: the shopping cart. */
public class CartPage extends BasePage {

    private final By productTitles = byDataTest("product-title");
    private final By cartTotal = byDataTest("cart-total");
    private final By proceedToCheckoutButton = byDataTest("proceed-1");
    private final By emptyCartMessage = By.xpath("//*[contains(text(),'The cart is empty')]");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    /** Finds the table row of one product, so we can act on that row only. */
    private String rowOf(String productName) {
        return "//tr[.//*[@data-test='product-title' and contains(normalize-space(),'" + productName + "')]]";
    }

    public List<String> getProductNames() {
        return getTextOfAll(productTitles);
    }

    public int getQuantity(String productName) {
        By quantity = By.xpath(rowOf(productName) + "//input[@data-test='product-quantity']");
        return Integer.parseInt(getValue(quantity));
    }

    public double getLineTotal(String productName) {
        By linePrice = By.xpath(rowOf(productName) + "//*[@data-test='line-price']");
        return toNumber(getText(linePrice));
    }

    public double getCartTotal() {
        return toNumber(getText(cartTotal));
    }

    /** Changes the quantity box and presses TAB so the cart recalculates. */
    public void updateQuantity(String productName, int newQuantity) {
        By quantity = By.xpath(rowOf(productName) + "//input[@data-test='product-quantity']");
        // Type the number and TAB in one go: the cart table refreshes after the change,
        // so the old input element can't be used again.
        type(quantity, newQuantity + Keys.TAB.toString());
    }

    /** Waits until a product's line total shows the expected amount (the cart updates via the API). */
    public void waitForLineTotal(String productName, double expected) {
        By linePrice = By.xpath(rowOf(productName) + "//*[@data-test='line-price']");
        waitForTextToBe(linePrice, String.format(Locale.US, "$%.2f", expected));
    }

    public void removeProduct(String productName) {
        By deleteButton = By.xpath(rowOf(productName) + "//a[contains(@class,'btn-danger')]");
        click(deleteButton);
    }

    public boolean isCartEmpty() {
        return isDisplayed(emptyCartMessage);
    }

    public CheckoutPage proceedToCheckout() {
        click(proceedToCheckoutButton);
        return new CheckoutPage(driver);
    }

    /** "$1,234.50" -> 1234.50 */
    private double toNumber(String price) {
        return Double.parseDouble(price.replace("$", "").replace(",", "").trim());
    }
}
