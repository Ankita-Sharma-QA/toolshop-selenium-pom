package com.toolshop.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.toolshop.utils.ConfigReader;

/**
 * Sets up the Extent HTML report.
 * After a run, open target/reports/ExtentReport.html in a browser.
 */
public class ExtentManager {

    private static ExtentReports extent;

    // One ExtentTest per thread, same idea as the driver in DriverFactory
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    public static synchronized ExtentReports getReport() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/reports/ExtentReport.html");
            spark.config().setDocumentTitle("Toolshop Automation Report");
            spark.config().setReportName("Toolshop UI Test Results");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", ConfigReader.get("baseUrl"));
            extent.setSystemInfo("Browser", ConfigReader.get("browser"));
            extent.setSystemInfo("Headless", ConfigReader.get("headless"));
        }
        return extent;
    }

    public static void setTest(ExtentTest test) {
        currentTest.set(test);
    }

    public static ExtentTest getTest() {
        return currentTest.get();
    }

    public static void removeTest() {
        currentTest.remove();
    }
}
