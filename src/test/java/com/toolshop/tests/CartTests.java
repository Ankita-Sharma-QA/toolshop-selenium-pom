package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.data.TestData;
import com.toolshop.pages.CartPage;
import com.toolshop.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CartTests extends BaseTest {

    /** Small helper used by the tests below: search for a product, open it and add it to the cart. */
    private void addProductToCart(String productName) {
        ProductPage productPage = header.goToHome()
                .searchFor(productName)
                .openProduct(productName);
        productPage.addToCart();
    }

    @Test(groups = {"regression"},
          description = "Two different products both appear in the cart")
    public void twoProductsAppearInCart() {
        addProductToCart(TestData.PRODUCT_PLIERS);
        header.waitForCartCount(1);
        addProductToCart(TestData.PRODUCT_HAMMER);
        header.waitForCartCount(2);

        CartPage cartPage = header.goToCart();
        List<String> productsInCart = cartPage.getProductNames();

        Assert.assertEquals(productsInCart.size(), 2);
        Assert.assertTrue(productsInCart.get(0).contains(TestData.PRODUCT_PLIERS)
                || productsInCart.get(1).contains(TestData.PRODUCT_PLIERS));
        Assert.assertTrue(productsInCart.get(0).contains(TestData.PRODUCT_HAMMER)
                || productsInCart.get(1).contains(TestData.PRODUCT_HAMMER));
    }

    @Test(groups = {"regression"},
          description = "Changing the quantity in the cart updates the line total and cart total")
    public void updatingQuantityUpdatesTotals() {
        addProductToCart(TestData.PRODUCT_PLIERS);
        header.waitForCartCount(1);

        CartPage cartPage = header.goToCart();
        double priceForOne = cartPage.getLineTotal(TestData.PRODUCT_PLIERS);

        cartPage.updateQuantity(TestData.PRODUCT_PLIERS, 3);
        double expectedTotal = priceForOne * 3;
        cartPage.waitForLineTotal(TestData.PRODUCT_PLIERS, expectedTotal);

        Assert.assertEquals(cartPage.getQuantity(TestData.PRODUCT_PLIERS), 3);
        Assert.assertEquals(cartPage.getLineTotal(TestData.PRODUCT_PLIERS), expectedTotal, 0.01);
        Assert.assertEquals(cartPage.getCartTotal(), expectedTotal, 0.01);
    }

    @Test(groups = {"regression"},
          description = "Removing the only product leaves the cart empty")
    public void removingProductEmptiesCart() {
        addProductToCart(TestData.PRODUCT_PLIERS);
        header.waitForCartCount(1);

        CartPage cartPage = header.goToCart();
        cartPage.removeProduct(TestData.PRODUCT_PLIERS);

        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after removing the product");
    }
}
