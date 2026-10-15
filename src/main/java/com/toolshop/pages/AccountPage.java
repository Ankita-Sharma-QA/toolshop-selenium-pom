package com.toolshop.pages;

import com.toolshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** "My account" page that opens after a successful login. */
public class AccountPage extends BasePage {

    private final By pageHeading = byDataTest("page-title");

    public AccountPage(WebDriver driver) {
        super(driver);
    }

    public String getHeading() {
        waitForUrlToContain("/account");
        return getText(pageHeading);
    }
}
