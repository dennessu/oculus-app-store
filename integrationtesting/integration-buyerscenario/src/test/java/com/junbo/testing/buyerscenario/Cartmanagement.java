/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.buyerscenario;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.Coupon;
import com.junbo.cart.spec.model.Offer;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.testing.common.apihelper.cart.CartService;
import com.junbo.testing.common.apihelper.cart.impl.CartServiceImpl;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.Component;
import com.junbo.testing.common.property.Priority;
import com.junbo.testing.common.property.Property;
import com.junbo.testing.common.property.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiefeng on 14-3-19.
 */
public class CartManagement {
    private LogHelper logger = new LogHelper(CartManagement.class);

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Incomplete,
            description = "get primary cart then add/remove items in cart",
            steps = {
                    ""
            }
    )
    @Test
    public void testAddRemoveItemsOnPrimaryCart() throws Exception {

        UserService us = UserServiceImpl.instance();
        CartService cs = CartServiceImpl.getInstance();

        //create a user
        String userId = us.PostUser();

        //get primary cart
        String primaryCartId = cs.getCartPrimary(userId);

        Cart primaryCart = Master.getInstance().getCart(primaryCartId);

        Assert.assertNotNull(primaryCart,"No Primary cart respond!");
        Assert.assertEquals(primaryCart.getOffers().size(),0);
        Assert.assertEquals(primaryCart.getCoupons().size(),0);
        Assert.assertTrue(primaryCart.getCartName().contains("primary"), "Primary cart name should include primary");

        List<OfferItem> offerItems = new ArrayList<>();
        List<CouponItem> couponItems = new ArrayList<>();
        Offer offer1 = new Offer();
        Offer offer2 = new Offer();

        Coupon couple1 = new Coupon();
        Coupon couple2 = new Coupon();

        //add a few offers and couples to primary cart and put cart
        OfferItem offerItem1 = new OfferItem();
        OfferItem offerItem2 = new OfferItem();
        offerItem1.setOffer(offer1);
        offerItem1.setQuantity(3L);
        offerItem1.setSelected(true);
        offerItem2.setOffer(offer2);
        offerItem2.setQuantity(2L);
        offerItem1.setSelected(true);
        CouponItem couponItem1= new CouponItem();
        CouponItem couponItem2 = new CouponItem();
        couponItem1.setCoupon(couple1);
        couponItem2.setCoupon(couple2);
        offerItems.add(offerItem1);
        offerItems.add(offerItem2);
        couponItems.add(couponItem1);
        couponItems.add(couponItem2);

        //update cart
        primaryCart.setOffers(offerItems);
        primaryCart.setCoupons(couponItems);

        String updatedCartId = cs.updateCart(userId, primaryCartId, primaryCart);

        Assert.assertNotNull(primaryCart,"No Primary cart respond!");
        Assert.assertEquals(primaryCart.getOffers().size(),0);
        Assert.assertEquals(primaryCart.getCoupons().size(),0);


    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Incomplete,
            description = "get primary cart then add/remove items in cart",
            steps = {
                    ""
            }
    )
    @Test
    public void testCartMerge() throws Exception {

        UserService us = UserServiceImpl.instance();
        CartService cs = CartServiceImpl.getInstance();

        //create a user
        String user1 = us.PostUser();

        //get primary cart
        String primaryCartId = cs.getCartPrimary(user1);

        Cart cart1 = Master.getInstance().getCart(primaryCartId);

        //add a few offers and couples to primary cart and put cart

    }

}
