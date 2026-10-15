package com.toolshop.pages;

import com.toolshop.base.BasePage;
import com.toolshop.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Home page = product overview with search, sort and filters.
 *
 * The page sets a data-test value on the results container while it loads:
 * "search_started" -> "search_completed", "sorting_started" -> "sorting_completed", etc.
 * We wait for the "completed" value instead of using Thread.sleep().
 */
public class HomePage extends BasePage {

    private final By searchInput = byDataTest("search-query");
    private final By searchButton = byDataTest("search-submit");
    private final By searchTerm = byDataTest("search-term");
    private final By noResultsMessage = byDataTest("no-results");
    private final By sortDropdown = byDataTest("sort");
    private final By productNames = byDataTest("product-name");
    private final By productPrices = byDataTest("product-price");

    private final By searchCompleted = byDataTest("search_completed");
    private final By sortingCompleted = byDataTest("sorting_completed");
    private final By filterCompleted = byDataTest("filter_completed");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        driver.get(ConfigReader.get("baseUrl"));
        try {
            waitForVisible(productNames);
        } catch (TimeoutException e) {
            // Give a clear message about what the browser actually shows
            String bodyText = driver.findElement(By.tagName("body")).getText().replaceAll("\\s+", " ");
            throw new RuntimeException("Home page did not load products. Title: '" + driver.getTitle()
                    + "', URL: " + driver.getCurrentUrl()
                    + ", page text: " + bodyText.substring(0, Math.min(400, bodyText.length())), e);
        }
        return this;
    }

    public HomePage searchFor(String text) {
        type(searchInput, text);
        click(searchButton);
        waitForVisible(searchCompleted);
        return this;
    }

    public String getSearchedTerm() {
        return getText(searchTerm);
    }

    public String getNoResultsMessage() {
        return getText(noResultsMessage);
    }

    /**
     * Sort options on the site:
     * "name,asc", "name,desc", "price,asc", "price,desc"
     */
    public HomePage sortBy(String option) {
        selectByValue(sortDropdown, option);
        waitForVisible(sortingCompleted);
        return this;
    }

    /** Ticks a category checkbox in the left filter panel, e.g. "Hammer". */
    public HomePage filterByCategory(String categoryName) {
        By checkbox = By.xpath("//label[normalize-space()='" + categoryName + "']/input");
        click(checkbox);
        waitForVisible(filterCompleted);
        return this;
    }

    public List<String> getProductNames() {
        return getTextOfAll(productNames);
    }

    /** Returns prices as numbers, e.g. "$14.15" -> 14.15 */
    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (String price : getTextOfAll(productPrices)) {
            prices.add(Double.parseDouble(price.replace("$", "").trim()));
        }
        return prices;
    }

    public ProductPage openProduct(String productName) {
        By product = By.xpath("//*[@data-test='product-name' and normalize-space()='" + productName + "']");
        click(product);
        return new ProductPage(driver);
    }
}
