package com.toolshop.base;

import com.toolshop.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent class for every page object.
 *
 * It keeps the common actions (click, type, read text, waits) in ONE place,
 * so page classes stay short and easy to read.
 * Every action waits for the element first, so we never need Thread.sleep().
 */
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait")));
    }

    /** Builds a locator for the site's data-test attribute, e.g. byDataTest("email"). */
    protected By byDataTest(String value) {
        return By.cssSelector("[data-test='" + value + "']");
    }

    protected WebElement waitForVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            // Add the page URL and any error/info messages shown on the page,
            // so the failure explains itself in the report.
            throw new TimeoutException("Element not visible: " + locator
                    + " | URL: " + driver.getCurrentUrl()
                    + " | Messages on page: " + getVisibleMessages(), e);
        }
    }

    /** Same as waitForVisible, but with a custom wait time for slow steps (e.g. placing an order). */
    protected WebElement waitForVisible(By locator, int seconds) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            return longWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException("Element not visible after " + seconds + "s: " + locator
                    + " | URL: " + driver.getCurrentUrl()
                    + " | Messages on page: " + getVisibleMessages(), e);
        }
    }

    /** Collects text from alert boxes and pop-up messages that are currently visible. */
    private String getVisibleMessages() {
        List<String> messages = new ArrayList<>();
        for (WebElement element : driver.findElements(By.cssSelector(".alert, .toast-message, .help-block"))) {
            if (element.isDisplayed() && !element.getText().isBlank()) {
                messages.add(element.getText().trim().replaceAll("\\s+", " "));
            }
        }
        return messages.isEmpty() ? "none" : String.join(" / ", messages);
    }

    protected void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            // A floating widget (chat bubble, toast message) is covering the element.
            // Clicking with JavaScript works even when something is on top.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        // Select all and type over it, instead of clear(), because Angular forms
        // don't always notice clear() and keep the old value.
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(text); // typing replaces the selected old text
    }

    /** Empties an input box (select all + delete). */
    protected void clearField(By locator) {
        WebElement element = waitForVisible(locator);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
    }

    /** Waits until an input box has some value, e.g. when the site fills it in automatically. */
    protected void waitForValueNotEmpty(By locator) {
        wait.until(d -> {
            try {
                String value = d.findElement(locator).getDomProperty("value");
                return value != null && !value.isBlank();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    /**
     * Reads the text of an element.
     * Angular sometimes re-draws an element right after we find it ("stale element"),
     * so we simply find it again and retry, up to 3 times.
     */
    protected String getText(By locator) {
        for (int attempt = 1; attempt < 3; attempt++) {
            try {
                return waitForVisible(locator).getText().trim();
            } catch (StaleElementReferenceException e) {
                // element was re-drawn, try again
            }
        }
        return waitForVisible(locator).getText().trim();
    }

    /** Reads the current value of an input box (with the same retry as getText). */
    protected String getValue(By locator) {
        for (int attempt = 1; attempt < 3; attempt++) {
            try {
                return waitForVisible(locator).getDomProperty("value");
            } catch (StaleElementReferenceException e) {
                // element was re-drawn, try again
            }
        }
        return waitForVisible(locator).getDomProperty("value");
    }

    protected List<String> getTextOfAll(By locator) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        List<String> texts = new ArrayList<>();
        for (WebElement element : driver.findElements(locator)) {
            texts.add(element.getText().trim());
        }
        return texts;
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void selectByValue(By locator, String value) {
        new Select(waitForVisible(locator)).selectByValue(value);
    }

    protected void waitForTextToBe(By locator, String text) {
        wait.until(ExpectedConditions.textToBe(locator, text));
    }

    protected void waitForUrlToContain(String text) {
        wait.until(ExpectedConditions.urlContains(text));
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}
