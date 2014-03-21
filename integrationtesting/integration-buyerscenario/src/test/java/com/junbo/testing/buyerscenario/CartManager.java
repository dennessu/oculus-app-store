/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.buyerscenario;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.CouponId;
import com.junbo.common.id.OfferId;
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

import java.util.*;


/**
 * Created by jiefeng on 14-3-19.
 */
public class CartManager {

    private LogHelper logger = new LogHelper(CartManager.class);

    //prepare a few test offers and test coupons
    private OfferId testOffer1 = new OfferId(100001L);
    private OfferId testOffer2 = new OfferId(100002L);
    private OfferId testOffer3 = new OfferId(100003L);

    private CouponId testCoupon1 = new CouponId(200001L);
    private CouponId testCoupon2 = new CouponId(200002L);

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Incomplete,
            description = "get primary cart then add/remove items in cart",
            steps = {
                    "1. Create an user" +
                            "/n 2. Get user's primary cart" +
                            "/n 3. Add a few offers and coupons to cart and update cart, validation" +
                            "/n 4 Remove offer&coupon and update quantity in the cart, add a new offer, update again, validation"
            }
    )
    @Test
    public void testAddRemoveItemsOnPrimaryCart() throws Exception {
        UserService us = UserServiceImpl.instance();
        CartService cs = CartServiceImpl.getInstance();

        //create a user
        String user = us.PostUser();

        //get primary cart
        String primaryCartId = cs.getCartPrimary(user);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        Assert.assertNotNull(primaryCart,"No Primary cart respond!");
        Assert.assertEquals(primaryCart.getOffers().size(),0);
        Assert.assertEquals(primaryCart.getCoupons().size(),0);
        Assert.assertTrue(primaryCart.getCartName().contains("primary"), "Primary cart name should include primary");

        //add a few offers and couples to primary cart and put cart
        //3 testOffer1 + 2 testOffer2 + testCoupon1 + testCoupon2
        addOrRemoveOfferInCart(primaryCart,testOffer1,3,true);
        addOrRemoveOfferInCart(primaryCart,testOffer2,2,true);
        addCouponInCart(primaryCart,testCoupon1);
        addCouponInCart(primaryCart,testCoupon2);

        //updateCart
        cs.updateCart(user, primaryCartId, primaryCart);

        // get primary cart again
        String updatedCartId =cs.getCartPrimary(user);
        Cart updatedCart = Master.getInstance().getCart(updatedCartId);
        Assert.assertEquals(updatedCartId,primaryCartId);

        //check two both items returned
        Assert.assertEquals(updatedCart.getOffers().size(),2);
        Assert.assertEquals(updatedCart.getCoupons().size(), 2);
        //check item quantity was returned correctly
        Assert.assertTrue(updatedCart.getOffers().get(0).getQuantity()==3);
        Assert.assertTrue(updatedCart.getOffers().get(1).getQuantity()==2);

        //reduce offer quantity, remove offer&coupon, add one new offer
        addOrRemoveOfferInCart(updatedCart, testOffer1, -1, true);
        addOrRemoveOfferInCart(updatedCart, testOffer2, -2, true);
        addOrRemoveOfferInCart(updatedCart, testOffer3, 1, true);
        removeCouponInCart(updatedCart,testCoupon2);

        //update cart and get primary cart again
        cs.updateCart(user, updatedCartId, updatedCart);
        String updatedCartId2 =cs.getCartPrimary(user);
        Cart updatedCart2 = Master.getInstance().getCart(updatedCartId2);
        Assert.assertEquals(updatedCartId2,updatedCartId);

        //check updated items returned correctly
        Assert.assertEquals(updatedCart2.getOffers().size(),2);
        Assert.assertEquals(updatedCart2.getCoupons().size(), 1);

        //check item quantity was updated correctly
        Assert.assertTrue(updatedCart.getOffers().get(0).getQuantity()==2);
        Assert.assertTrue(updatedCart.getOffers().get(2).getQuantity()==1);
    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Incomplete,
            description = "test cart merge",
            steps = {
                    "1. Create two users" +
                            "/n 2 Get primary cart for both users" +
                            "/n 3 Add a few offers and coupon(s) to user1's cart" +
                            "/n 4 Post user1's new cart" +
                            "/n 5 Add a few offers and coupon(s) to user2's cart" +
                            "/n 6 Merge user2's cart to user1" +
                            "/n verify merge result"
            }
    )
    @Test
    public void testCartMerge() throws Exception {
        UserService us = UserServiceImpl.instance();
        CartService cs = CartServiceImpl.getInstance();

        //create two users
        String user1 = us.PostUser();
        String user2 = us.PostUser();

        //add a few offers and couples to user1's primary cart
        //3 testOffer1 + 2 testOffer2 + testCoupon1
        String primaryCartId1 = cs.getCartPrimary(user1);
        Cart primaryCart1 = Master.getInstance().getCart(primaryCartId1);
        addOrRemoveOfferInCart(primaryCart1,testOffer1,3,true);
        addOrRemoveOfferInCart(primaryCart1,testOffer2,2,true);
        addCouponInCart(primaryCart1,testCoupon1);
        cs.updateCart(user1, primaryCartId1, primaryCart1);

        //add a few offers and couples to user2's primary cart
        //2 testOffer1 + 3 testOffer3 + testCoupon2
        String primaryCartId2 = cs.getCartPrimary(user2);
        Cart primaryCart2 = Master.getInstance().getCart(primaryCartId1);
        addOrRemoveOfferInCart(primaryCart2,testOffer1,2,true);
        addOrRemoveOfferInCart(primaryCart2,testOffer3,3,true);
        addCouponInCart(primaryCart2,testCoupon2);
        //cs.updateCart(user2, primaryCartId2, primaryCart2);

        //merge user2's cart to user1's primary cart
        cs.mergeCart(user1,primaryCartId1, primaryCart2);


        //verify merge result
        //5 testOffer1 + 2 testOffer2 + 3 testOffer3 + testCoupon1 + testCoupon2
        //check updated items returned correctly
        String mergedCartId =cs.getCartPrimary(user1);
        Cart mergedCart = Master.getInstance().getCart(mergedCartId);
        Assert.assertEquals(mergedCart.getOffers().size(),3);
        Assert.assertEquals(mergedCart.getCoupons().size(), 2);

        //check item quantity was updated correctly
        Assert.assertTrue(mergedCart.getOffers().get(0).getQuantity()==5);
        Assert.assertTrue(mergedCart.getOffers().get(1).getQuantity()==2);
        Assert.assertTrue(mergedCart.getOffers().get(2).getQuantity()==3);
    }

//helper functions:
    private void addOrRemoveOfferInCart(Cart cart, OfferId offerId, long quantity, boolean selected){
        if (quantity==0) return;

        List<OfferItem> curOffers = cart.getOffers();
        if (curOffers == null){
            curOffers = new ArrayList<OfferItem>();
            cart.setOffers(curOffers);
            }

        boolean existingOffer = false;
        for(OfferItem oi : curOffers){
            if (oi.getOffer() == offerId){
                long newQuantity = oi.getQuantity() + quantity;
                if (newQuantity==0) {
                    curOffers.remove(oi);
                }
                else{
                    oi.setQuantity(newQuantity);
                }
                existingOffer = true;
                break;
            }
        }

        if(!existingOffer){
            OfferItem offerItem = new OfferItem();
            offerItem.setOffer(offerId);
            offerItem.setQuantity(quantity);
            offerItem.setSelected(selected);
            curOffers.add(offerItem);
        }
    }

    private void addCouponInCart(Cart cart, CouponId couponId){
        List<CouponItem> curCoupons = cart.getCoupons();
        if (curCoupons == null){
            curCoupons = new ArrayList<CouponItem>();
            cart.setCoupons(curCoupons);
        }

        CouponItem couponItem = new CouponItem();
        couponItem.setCoupon(couponId);
        curCoupons.add(couponItem);
    }

    private void removeCouponInCart(Cart cart, CouponId couponId){
        List<CouponItem> curCoupons = cart.getCoupons();
        if (curCoupons == null){
            return;
        }

        for(CouponItem ci : curCoupons){
            if (ci.getCoupon() == couponId){
                curCoupons.remove(ci);
            }
        }
    }
}
