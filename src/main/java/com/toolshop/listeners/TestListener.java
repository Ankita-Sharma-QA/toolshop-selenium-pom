package com.toolshop.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.toolshop.reports.ExtentManager;
import com.toolshop.utils.DriverFactory;
import com.toolshop.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener: it runs automatically around every test (registered in the testng xml files).
 *  - Creates an entry in the Extent report for each test
 *  - Marks it as pass / fail / skip
 *  - Takes a screenshot when a test fails
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        ExtentTest test = ExtentManager.getReport().createTest(testName, description);
        test.assignCategory(result.getMethod().getGroups());
        ExtentManager.setTest(test);

        System.out.println("STARTED: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().pass("Test passed");
        ExtentManager.removeTest();
        System.out.println("PASSED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentManager.getTest();
        test.fail(result.getThrowable());

        // BaseTest takes the screenshot before closing the browser and stores it on the result.
        // If the browser is still open here, we take it ourselves.
        String base64 = (String) result.getAttribute("screenshot");
        WebDriver driver = DriverFactory.getDriver();
        if (base64 == null && driver != null) {
            ScreenshotUtil.saveScreenshot(driver, testName);
            base64 = ScreenshotUtil.getBase64Screenshot(driver);
        }
        if (base64 != null) {
            test.fail("Screenshot at failure",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
        }
        ExtentManager.removeTest();
        System.out.println("FAILED: " + testName + " -> " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentManager.getTest();
        if (result.wasRetried()) {
            // This failed attempt is being retried, so remove it from the report.
            // Only the final attempt (pass or fail) should be shown.
            if (test != null) {
                ExtentManager.getReport().removeTest(test);
            }
            ExtentManager.removeTest();
            System.out.println("WILL RETRY: " + result.getMethod().getMethodName());
            return;
        }
        if (test != null) {
            test.skip(result.getThrowable() != null ? result.getThrowable().getMessage() : "Test skipped");
        }
        ExtentManager.removeTest();
        System.out.println("SKIPPED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Writes everything into the HTML file
        ExtentManager.getReport().flush();
    }
}
