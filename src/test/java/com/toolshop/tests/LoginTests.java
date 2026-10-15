package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.data.TestData;
import com.toolshop.pages.AccountPage;
import com.toolshop.pages.LoginPage;
import com.toolshop.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test(groups = {"smoke", "regression"},
          description = "User can log in with valid email and password")
    public void validLoginOpensMyAccount() {
        AccountPage accountPage = loginAsDefaultUser();

        Assert.assertEquals(accountPage.getHeading(), "My account");
        Assert.assertEquals(header.getLoggedInUserName(), ConfigReader.get("userFullName"));
    }

    @Test(groups = {"regression"},
          description = "Wrong password shows an error message")
    public void invalidPasswordShowsError() {
        LoginPage loginPage = header.goToSignIn();
        loginPage.enterEmail(ConfigReader.get("userEmail"));
        loginPage.enterPassword(TestData.WRONG_PASSWORD);
        loginPage.clickLogin();

        Assert.assertEquals(loginPage.getLoginErrorMessage(), "Invalid email or password");
    }

    @Test(groups = {"regression"},
          description = "Submitting an empty form shows 'required' messages")
    public void emptyLoginFormShowsRequiredErrors() {
        LoginPage loginPage = header.goToSignIn();
        loginPage.clickLogin();

        Assert.assertEquals(loginPage.getEmailErrorMessage(), "Email is required");
        Assert.assertEquals(loginPage.getPasswordErrorMessage(), "Password is required");
    }

    @Test(groups = {"regression"},
          description = "User can sign out after logging in")
    public void userCanSignOut() {
        loginAsDefaultUser();
        header.signOut();

        Assert.assertTrue(header.isSignInLinkDisplayed(), "Sign in link should be visible after sign out");
    }
}
