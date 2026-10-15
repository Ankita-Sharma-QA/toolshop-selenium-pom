package com.toolshop.pages;

import com.toolshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By emailInput = byDataTest("email");
    private final By passwordInput = byDataTest("password");
    private final By loginButton = byDataTest("login-submit");
    private final By loginError = byDataTest("login-error");
    private final By emailError = byDataTest("email-error");
    private final By passwordError = byDataTest("password-error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterEmail(String email) {
        type(emailInput, email);
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    public void clickLogin() {
        click(loginButton);
    }

    /** Use this for a successful login. */
    public AccountPage loginAs(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
        return new AccountPage(driver);
    }

    public String getLoginErrorMessage() {
        return getText(loginError);
    }

    public String getEmailErrorMessage() {
        return getText(emailError);
    }

    public String getPasswordErrorMessage() {
        return getText(passwordError);
    }
}
