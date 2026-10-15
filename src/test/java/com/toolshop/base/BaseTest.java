package com.toolshop.base;

import com.toolshop.pages.AccountPage;
import com.toolshop.pages.HeaderSection;
import com.toolshop.pages.HomePage;
import com.toolshop.pages.LoginPage;
import com.toolshop.utils.ConfigReader;
import com.toolshop.utils.DriverFactory;
import com.toolshop.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Parent class for every test class.
 * Opens a fresh browser before each test and closes it after,
 * so tests never depend on each other.
 */
public class BaseTest {

    protected WebDriver driver;
    protected HomePage homePage;
    protected HeaderSection header;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();

        homePage = new HomePage(driver).open();
        header = new HeaderSection(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Take the screenshot here, while the browser is still open.
        // TestListener adds it to the HTML report.
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            String testName = result.getMethod().getMethodName();
            ScreenshotUtil.saveScreenshot(driver, testName);
            result.setAttribute("screenshot", ScreenshotUtil.getBase64Screenshot(driver));
        }
        DriverFactory.quitDriver();
    }

    /** Logs in with the main demo user from config.properties. */
    protected AccountPage loginAsDefaultUser() {
        return loginAs(ConfigReader.get("userEmail"), ConfigReader.get("userPassword"));
    }

    /** Logs in with any user and waits until the account page has loaded. */
    protected AccountPage loginAs(String email, String password) {
        LoginPage loginPage = header.goToSignIn();
        AccountPage accountPage = loginPage.loginAs(email, password);
        accountPage.getHeading(); // waits until the account page has loaded
        return accountPage;
    }
}
