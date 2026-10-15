package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.data.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchAndFilterTests extends BaseTest {

    @Test(groups = {"smoke", "regression"},
          description = "Search returns only products that match the search word")
    public void searchReturnsMatchingProducts() {
        homePage.searchFor(TestData.SEARCH_TERM);

        Assert.assertEquals(homePage.getSearchedTerm(), TestData.SEARCH_TERM);

        List<String> names = homePage.getProductNames();
        Assert.assertFalse(names.isEmpty(), "Search should return at least one product");
        for (String name : names) {
            Assert.assertTrue(name.contains(TestData.SEARCH_TERM),
                    "Product '" + name + "' does not contain '" + TestData.SEARCH_TERM + "'");
        }
    }

    @Test(groups = {"regression"},
          description = "Searching for an unknown product shows 'no products found'")
    public void searchWithNoResultsShowsMessage() {
        homePage.searchFor(TestData.SEARCH_NO_RESULTS);

        Assert.assertEquals(homePage.getNoResultsMessage(), "There are no products found.");
    }

    @Test(groups = {"regression"},
          description = "Sort by price (low - high) lists cheapest product first")
    public void sortByPriceLowToHigh() {
        homePage.sortBy("price,asc");

        List<Double> actualPrices = homePage.getProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices);

        Assert.assertEquals(actualPrices, expectedPrices, "Prices are not in ascending order");
    }

    @Test(groups = {"regression"},
          description = "Sort by name (Z - A) lists products in reverse alphabetical order")
    public void sortByNameZToA() {
        homePage.sortBy("name,desc");

        List<String> actualNames = homePage.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        expectedNames.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Assert.assertEquals(actualNames, expectedNames, "Names are not in Z-A order");
    }

    @Test(groups = {"regression"},
          description = "Filtering by the Hammer category shows only hammers")
    public void filterByCategoryShowsOnlyThatCategory() {
        homePage.filterByCategory(TestData.CATEGORY_HAMMER);

        List<String> names = homePage.getProductNames();
        Assert.assertFalse(names.isEmpty(), "Filter should return at least one product");
        for (String name : names) {
            Assert.assertTrue(name.toLowerCase().contains("hammer"),
                    "Product '" + name + "' is not a hammer");
        }
    }
}
