package com.toolshop.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Takes screenshots when a test fails.
 */
public class ScreenshotUtil {

    private static final String SCREENSHOT_FOLDER = "target/screenshots/";

    /** Saves a PNG file and returns its path. */
    public static String saveScreenshot(WebDriver driver, String testName) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = SCREENSHOT_FOLDER + testName + "_" + time + ".png";
        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get(filePath);
            Files.createDirectories(destination.getParent());
            Files.copy(source.toPath(), destination);
        } catch (IOException e) {
            System.out.println("Could not save screenshot: " + e.getMessage());
        }
        return filePath;
    }

    /** Returns the screenshot as Base64 text, so it can be embedded inside the HTML report. */
    public static String getBase64Screenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }
}
