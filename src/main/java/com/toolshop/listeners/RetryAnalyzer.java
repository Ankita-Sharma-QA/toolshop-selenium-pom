package com.toolshop.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Re-runs a failed test once.
 *
 * UI tests against a live website can fail because of a slow network,
 * not because of a real bug. One retry removes that noise.
 * If the test fails twice, it is reported as a real failure.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY = 1;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            System.out.println("RETRYING: " + result.getMethod().getMethodName() + " (attempt " + (retryCount + 1) + ")");
            return true;
        }
        return false;
    }
}
