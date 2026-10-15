package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.data.TestData;
import com.toolshop.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductTests extends BaseTest {

    @Test(groups = {"smoke", "regression"},
          description = "Product details page shows name, price and description")
    public void productDetailsAreDisplayed() {
        ProductPage productPage = homePage
                .searchFor(TestData.PRODUCT_PLIERS)
                .openProduct(TestData.PRODUCT_PLIERS);

        Assert.assertEquals(productPage.getProductName(), TestData.PRODUCT_PLIERS);
        Assert.assertTrue(productPage.getUnitPrice() > 0, "Price should be greater than 0");
        Assert.assertTrue(productPage.isDescriptionDisplayed(), "Description should be visible");
    }

    @Test(groups = {"regression"},
          description = "Plus and minus buttons change the quantity")
    public void quantityButtonsChangeQuantity() {
        ProductPage productPage = homePage
                .searchFor(TestData.PRODUCT_PLIERS)
                .openProduct(TestData.PRODUCT_PLIERS);

        Assert.assertEquals(productPage.getQuantity(), 1, "Default quantity should be 1");

        productPage.increaseQuantity();
        productPage.increaseQuantity();
        Assert.assertEquals(productPage.getQuantity(), 3);

        productPage.decreaseQuantity();
        Assert.assertEquals(productPage.getQuantity(), 2);
    }

    @Test(groups = {"smoke", "regression"},
          description = "Adding a product shows a message and updates the cart badge")
    public void addToCartUpdatesCartBadge() {
        ProductPage productPage = homePage
                .searchFor(TestData.PRODUCT_PLIERS)
                .openProduct(TestData.PRODUCT_PLIERS);

        String message = productPage.addToCart();

        Assert.assertEquals(message, "Product added to shopping cart.");
        Assert.assertEquals(header.waitForCartCount(1), 1);
    }
}
