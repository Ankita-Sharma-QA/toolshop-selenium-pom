package com.toolshop.pages;

import com.toolshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Product details page. */
public class ProductPage extends BasePage {

    private final By productName = byDataTest("product-name");
    private final By unitPrice = byDataTest("unit-price");
    private final By description = byDataTest("product-description");
    private final By quantityInput = byDataTest("quantity");
    private final By increaseQuantityButton = byDataTest("increase-quantity");
    private final By decreaseQuantityButton = byDataTest("decrease-quantity");
    private final By addToCartButton = byDataTest("add-to-cart");

    // Pop-up message shown at the top right (ngx-toastr library)
    private final By toastMessage = By.cssSelector("#toast-container .toast-message");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getProductName() {
        return getText(productName);
    }

    public double getUnitPrice() {
        return Double.parseDouble(getText(unitPrice));
    }

    public boolean isDescriptionDisplayed() {
        return isDisplayed(description);
    }

    public int getQuantity() {
        return Integer.parseInt(getValue(quantityInput));
    }

    public void increaseQuantity() {
        click(increaseQuantityButton);
    }

    public void decreaseQuantity() {
        click(decreaseQuantityButton);
    }

    public void setQuantity(int quantity) {
        type(quantityInput, String.valueOf(quantity));
    }

    /** Clicks "Add to cart" and returns the pop-up message text. */
    public String addToCart() {
        click(addToCartButton);
        String message = getText(toastMessage);
        waitForInvisible(toastMessage); // wait so the pop-up doesn't cover other buttons
        return message;
    }
}
