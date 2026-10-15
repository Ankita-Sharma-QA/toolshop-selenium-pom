package com.toolshop.pages;

import com.toolshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * The navigation bar at the top of every page.
 * It is its own class because it is shared by all pages.
 */
public class HeaderSection extends BasePage {

    private final By homeLink = byDataTest("nav-home");
    private final By signInLink = byDataTest("nav-sign-in");
    private final By userMenu = byDataTest("nav-menu");
    private final By signOutLink = byDataTest("nav-sign-out");
    private final By cartLink = byDataTest("nav-cart");
    private final By cartQuantity = byDataTest("cart-quantity");

    public HeaderSection(WebDriver driver) {
        super(driver);
    }

    public HomePage goToHome() {
        click(homeLink);
        return new HomePage(driver);
    }

    public LoginPage goToSignIn() {
        click(signInLink);
        return new LoginPage(driver);
    }

    public CartPage goToCart() {
        click(cartLink);
        return new CartPage(driver);
    }

    /** Name shown in the top-right menu after login, e.g. "Jane Doe". */
    public String getLoggedInUserName() {
        return getText(userMenu);
    }

    public boolean isSignInLinkDisplayed() {
        return isDisplayed(signInLink);
    }

    public void signOut() {
        click(userMenu);
        click(signOutLink);
    }

    /** Waits until the cart badge shows the expected number, then returns it. */
    public int waitForCartCount(int expected) {
        waitForTextToBe(cartQuantity, String.valueOf(expected));
        return Integer.parseInt(getText(cartQuantity));
    }
}
