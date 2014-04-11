/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.cart;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.OfferId;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiefeng on 14-4-1.
 */
public class CartTesting extends TestClass {

    private LogHelper logger = new LogHelper(CartTesting.class);

    //prepare a few test offers and test coupons
    private OfferId testOffer1 = new OfferId(100001L);
    private OfferId testOffer2 = new OfferId(100002L);
    private OfferId testOffer3 = new OfferId(100003L);

    private String testCoupon1 = new String("200001L");
    private String testCoupon2 = new String("200002L");


    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /users/{key}/carts/primary",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "get primary cart",
            steps = {
                    "1. Create an user" +
                            "/n 2. Get user's primary cart" +
                            "/n 3, Validation: response & cart name"
            }
    )
    @Test
    public void testGetPrimaryCart() throws Exception {
        UserService us = UserServiceImpl.instance();
        //create a user
        String user = us.PostUser();
        //get primary cart
        String primaryCartId = CartService.getCartPrimary(user);
        logger.LogSample("Get user's primary cart");
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        Assert.assertNotNull(primaryCart, "No Primary cart respond!");
        Assert.assertEquals(primaryCart.getOffers().size(), 0);//no offers when first call get primary cart.
        Assert.assertEquals(primaryCart.getCouponCodes().size(), 0); //no coupons when first call get primary cart.
        Assert.assertTrue(primaryCart.getCartName().contains("primary"), "Primary cart name should include primary");
    }


    @Property(
            priority = Priority.Dailies,
            features = "POST /users/{key}/carts",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "post a new named cart",
            steps = {
                    "1. Create an user" +
                            "/n 2. post a named cart for user" +
                            "/n 3. Validation"
            }
    )
    @Test
    public void testPostCart() throws Exception {

        UserService us = UserServiceImpl.instance();
        //create a user
        String user = us.PostUser();

        Cart cart = new Cart();
        String cartName = "Automation Testing";
        cart.setCartName(cartName);
        addOrRemoveOfferInCart(cart, testOffer1, 2, true);
        addOrRemoveOfferInCart(cart, testOffer2, 1, true);
        addCouponInCart(cart, testCoupon1);

        //post offer
        String newCartId = CartService.addCart(user, cart);
        Cart rtnCart = Master.getInstance().getCart(newCartId);

        //validation
        Assert.assertEquals(rtnCart.getCartName(), cartName);
        checkOfferQuantity(rtnCart, testOffer1, 2L);
        checkOfferQuantity(rtnCart, testOffer2, 1L);
        checkCouponExist(rtnCart, testCoupon1);
    }


    @Property(
            priority = Priority.Dailies,
            features = "POST /users/{keyUser}/carts/{keyCart}/merge",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "merge an cart to user's primary cart",
            steps = {
                    "1. Create user1" +
                            "/n 2. Get user1's primary cart " +
                            "/n 3. Add a few offers and coupons to user1's primary cart" +
                            "/n 4. Create user2" +
                            "/n 5. get user2's primary cart" +
                            "/n 6. Add a few offers and coupons to user2's primary cart" +
                            "/n 7. Merge user2's cart to user1's primary cart" +
                            "/n 8. Check user1's primary cart to verify offers and Coupons are merged correctly"
            }
    )
    @Test
    public void testMergeCart() throws Exception {
        UserService us = UserServiceImpl.instance();
        //create two users
        String user1 = us.PostUser();
        String user2 = us.PostUser();

        //add a few offers and couples to user1's primary cart
        //3 testOffer1 + 2 testOffer2 + testCoupon1
        String primaryCartId1 = CartService.getCartPrimary(user1);
        Cart primaryCart1 = Master.getInstance().getCart(primaryCartId1);
        addOrRemoveOfferInCart(primaryCart1, testOffer1, 3, true);
        addOrRemoveOfferInCart(primaryCart1, testOffer2, 2, true);
        addCouponInCart(primaryCart1, testCoupon1);
        CartService.updateCart(user1, primaryCartId1, primaryCart1);

        //add a few offers and couples to user2's primary cart
        //2 testOffer1 + 3 testOffer3 + testCoupon2
        String primaryCartId2 = CartService.getCartPrimary(user2);
        Cart primaryCart2 = Master.getInstance().getCart(primaryCartId2);
        addOrRemoveOfferInCart(primaryCart2, testOffer1, 2, true);
        addOrRemoveOfferInCart(primaryCart2, testOffer3, 3, true);
        addCouponInCart(primaryCart2, testCoupon2);
        CartService.updateCart(user2, primaryCartId2, primaryCart2);

        //merge user2's cart to user1's primary cart
        logger.LogSample("Merge cart included in request body to an user's cart in URI");
        CartService.mergeCart(user1, primaryCartId1, primaryCart2);
        //verify merge result
        //5 testOffer1 + 2 testOffer2 + 3 testOffer3 + testCoupon1 + testCoupon2
        //check updated items returned correctly
        String mergedCartId = CartService.getCartPrimary(user1);
        Cart mergedCart = Master.getInstance().getCart(mergedCartId);
        Assert.assertEquals(mergedCart.getOffers().size(), 3);
        Assert.assertEquals(mergedCart.getCouponCodes().size(), 2);

        //check item quantity was updated correctly
        Assert.assertTrue(checkOfferQuantity(mergedCart, testOffer1, 5L));
        Assert.assertTrue(checkOfferQuantity(mergedCart, testOffer2, 2L));
        Assert.assertTrue(checkOfferQuantity(mergedCart, testOffer3, 3L));
    }

    @Property(
            priority = Priority.Dailies,
            features = "PUT /users/{keyUser}/carts/{keyCart}",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "update shopping cart",
            steps = {
                    "1. Create an user" +
                            "/n 2. Get user's primary cart" +
                            "/n 3. Add a few offers and coupons to user's primary cart" +
                            "/n 4. Update offers and coupons in cart" +
                            "/n 5. Validation:"
            }
    )
    @Test
    public void testUpdateCart() throws Exception {

        UserService us = UserServiceImpl.instance();
        String user = us.PostUser();
        String primaryCartId = CartService.getCartPrimary(user);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        addOrRemoveOfferInCart(primaryCart, testOffer1, 3, true);
        addOrRemoveOfferInCart(primaryCart, testOffer2, 2, true);
        addCouponInCart(primaryCart, testCoupon1);
        CartService.updateCart(user, primaryCartId, primaryCart);
        Cart newCart = new Cart();
        newCart.setId(primaryCart.getId());
        addOrRemoveOfferInCart(newCart, testOffer2, 1, true);
        addOrRemoveOfferInCart(newCart, testOffer3, 3, true);
        addCouponInCart(newCart, testCoupon2);
        String updateCartId = CartService.updateCart(user, primaryCartId, newCart);
        logger.LogSample("update user's cart");
        Cart rtnCart = Master.getInstance().getCart(updateCartId);

        //validation:
        Assert.assertTrue(checkOfferQuantity(rtnCart, testOffer2, 1L));
        Assert.assertTrue(checkOfferQuantity(rtnCart, testOffer3, 3L));
        Assert.assertTrue(!checkOfferExist(rtnCart, testOffer1));
        Assert.assertTrue(checkCouponExist(rtnCart, testCoupon2));
        Assert.assertTrue(!checkCouponExist(rtnCart, testCoupon1));
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /users/{key}/carts(?cartName)",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "get user's shopping cart by cart name",
            steps = {
                    "1. Create an user" +
                            "/n 2. post a named cart for user" +
                            "/n 3. get cart by cart name"
            }
    )
    @Test
    public void testGetCartByName() throws Exception {
        UserService us = UserServiceImpl.instance();
        //create a user
        String user = us.PostUser();

        Cart cart = new Cart();
        String cartName = "AutomationTesting";
        cart.setCartName(cartName);
        addOrRemoveOfferInCart(cart, testOffer1, 3, true);
        addOrRemoveOfferInCart(cart, testOffer2, 5, true);
        addCouponInCart(cart, testCoupon1);

        //post offer
        CartService.addCart(user, cart);

        //get offer by name
        logger.LogSample("Get user's cart according to cart name");
        String rtnCartId = CartService.getCartByName(user, cartName);
        Cart rtnCart = Master.getInstance().getCart(rtnCartId);
        //validation
        Assert.assertEquals(rtnCart.getCartName(), cartName);
        checkOfferQuantity(rtnCart, testOffer1, 3L);
        checkOfferQuantity(rtnCart, testOffer2, 5L);
        checkCouponExist(rtnCart, testCoupon1);
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /users/{keyUser}/carts/{keyCart}",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "get user's shopping cart by cart id",
            steps = {
                    "1. Create an user" +
                            "/n 2. post a named cart for user" +
                            "/n 3. get cart by cart id"
            }
    )
    @Test
    public void testGetCartById() throws Exception {
        UserService us = UserServiceImpl.instance();
        //create a user
        String user = us.PostUser();

        Cart cart = new Cart();
        String cartName = "Automation Testing";
        cart.setCartName(cartName);
        addOrRemoveOfferInCart(cart, testOffer1, 3, true);
        addOrRemoveOfferInCart(cart, testOffer2, 5, true);
        addCouponInCart(cart, testCoupon1);

        //post offer
        String newCartId = CartService.addCart(user, cart);

        //get offer by name
        logger.LogSample("Get user's cart by cart id");
        String rtnCartId = CartService.getCart(user, newCartId);
        Cart rtnCart = Master.getInstance().getCart(rtnCartId);
        //validation
        Assert.assertEquals(rtnCart.getCartName(), cartName);
        checkOfferQuantity(rtnCart, testOffer1, 3L);
        checkOfferQuantity(rtnCart, testOffer2, 5L);
        checkCouponExist(rtnCart, testCoupon1);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "GET /users/{invalidkey}/carts/primary",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "update shopping cart",
            steps = {
                    "1. Create an user" +
                            "/n 2. Get user's primary cart with an invalid user id" +
                            "/n 3. Check response code"
            }
    )
    @Test
    public void testGetPrimaryCartWithInvalidUser() throws Exception {

        String invalidUserId = "12345";
        logger.LogSample("call get primary cart with invalid user id");
        CartService.getCartPrimary(invalidUserId, 404);//response code to be defined
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "GET /users/{keyUser}/carts/{invalidKeyCart}",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Get user's cart with an invalid cart id",
            steps = {
                    "1. Create an user" +
                            "/n 2. post a new cart" +
                            "/n 3. Get user's  cart with an invalid cart id" +
                            "/n 4. Check response code"
            }
    )
    @Test
    public void testGetCartWithInvalidCartId() throws Exception {

        UserService us = UserServiceImpl.instance();
        //create a user
        String user = us.PostUser();
        //Create a cart
        Cart cart = new Cart();
        String cartName = "Automation Testing";
        cart.setCartName(cartName);
        addOrRemoveOfferInCart(cart, testOffer1, 3, true);
        addOrRemoveOfferInCart(cart, testOffer2, 5, true);
        addCouponInCart(cart, testCoupon1);
        CartService.addCart(user, cart);

        String invalidCartId = "12345";
        logger.LogSample("Get user's cart with an invalid cart id");
        CartService.getCart(user, invalidCartId, 404);
    }

    //helper functions:
    private void addOrRemoveOfferInCart(Cart cart, OfferId offerId, long quantity, boolean selected) {
        if (quantity == 0) return;

        List<OfferItem> curOffers = cart.getOffers();
        if (curOffers == null) {
            curOffers = new ArrayList<OfferItem>();
            cart.setOffers(curOffers);
        }

        boolean existingOffer = false;
        for (OfferItem oi : curOffers) {
            if (oi.getOffer().equals(offerId)) {
                long newQuantity = oi.getQuantity() + quantity;
                if (newQuantity == 0) {
                    curOffers.remove(oi);
                } else {
                    oi.setQuantity(newQuantity);
                }
                existingOffer = true;
                break;
            }
        }

        if (!existingOffer) {
            OfferItem offerItem = new OfferItem();
            offerItem.setOffer(offerId);
            offerItem.setQuantity(quantity);
            offerItem.setSelected(selected);
            curOffers.add(offerItem);
        }
    }

    private void addCouponInCart(Cart cart, String couponId) {
        List<String> curCoupons = cart.getCouponCodes();
        if (curCoupons == null) {
            curCoupons = new ArrayList<>();
            cart.setCouponCodes(curCoupons);
        }

        curCoupons.add(couponId);
    }

    private void removeCouponInCart(Cart cart, String couponId) {
        List<String> curCoupons = cart.getCouponCodes();
        if (curCoupons == null) {
            return;
        }

        for (String ci : curCoupons) {
            if (ci.equals(couponId)) {
                curCoupons.remove(ci);
                break;
            }
        }
    }

    private boolean checkOfferQuantity(Cart cart, OfferId offerId, Long expectedQuantity) {
        List<OfferItem> curOffers = cart.getOffers();
        if (curOffers == null) return false;
        for (OfferItem oi : curOffers) {
            if (oi.getOffer().equals(offerId)) {
                return (oi.getQuantity().equals(expectedQuantity));
            }
        }
        return false;
    }

    private boolean checkCouponExist(Cart cart, String couponId) {
        List<String> curCoupons = cart.getCouponCodes();
        if (curCoupons == null) return false;
        for (String ci : curCoupons) {
            if (ci.equals(couponId)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOfferExist(Cart cart, OfferId offerId) {
        List<OfferItem> curOffers = cart.getOffers();
        if (curOffers == null) return false;
        for (OfferItem oi : curOffers) {
            if (oi.getOffer().equals(offerId)) {
                return true;
            }
        }
        return false;
    }
}
