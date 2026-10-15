package com.toolshop.utils;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates and stores the WebDriver.
 *
 * Why ThreadLocal?
 * When tests run in parallel, each thread needs its OWN browser.
 * ThreadLocal gives every thread a separate copy of the driver,
 * so two tests never click in each other's browser.
 *
 * Driver binaries (chromedriver, geckodriver) are downloaded automatically
 * by Selenium Manager, which is built into Selenium 4.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver webDriver;

        switch (browser) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                webDriver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                webDriver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--disable-notifications");

                // Turn off Chrome's password manager. After logging in with a demo password,
                // Chrome shows a "password found in a data breach" pop-up that steals keyboard
                // focus, so typing in the next fields gets lost.
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.addArguments("--disable-features=PasswordLeakDetection");
                webDriver = new ChromeDriver(chromeOptions);
                break;
        }

        webDriver.manage().window().setSize(new Dimension(1920, 1080));
        driver.set(webDriver);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
