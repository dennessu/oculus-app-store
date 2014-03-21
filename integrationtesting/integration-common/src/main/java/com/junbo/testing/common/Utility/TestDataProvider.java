/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.Utility;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.OfferId;
import com.junbo.identity.spec.model.user.User;
import com.junbo.testing.common.apihelper.cart.CartService;
import com.junbo.testing.common.apihelper.cart.impl.CartServiceImpl;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.apihelper.catalog.OfferService;
import com.junbo.testing.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.testing.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.EnumHelper.UserStatus;
import com.junbo.testing.common.libs.RandomFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 3/20/14.
 */
public class TestDataProvider {
    private UserService identityClient = UserServiceImpl.instance();
    private ItemService itemClient = ItemServiceImpl.instance();
    private OfferService offerClient = OfferServiceImpl.instance();
    private CartService cartClient = CartServiceImpl.getInstance();

    public TestDataProvider() {
    }

    public String createUser(String email, String password, UserStatus status) throws Exception {
        User userToPost = new User();
        userToPost.setUserName(email);
        userToPost.setPassword(password);
        userToPost.setStatus(status.getStatus());

        return identityClient.PostUser(userToPost);
    }

    public String createUser() throws Exception {
        return identityClient.PostUser();
    }

    public String getUserByUid(String userId) throws Exception {
        return identityClient.GetUserByUserId(userId);
    }

    public String postDefaultItem() throws Exception {
        return itemClient.postDefaultItem();
    }

    public String postDefaultOffer() throws Exception {
        return offerClient.postDefaultOffer();
    }

    public void postOffersToPrimaryCart(String uid, ArrayList<String> offers) throws Exception {
        String primaryCartId = cartClient.getCartPrimary(uid);
        Cart primaryCart = Master.getInstance().getCart(primaryCartId);
        List<OfferItem> offerItemList = new ArrayList<>();
        List<CouponItem> couponItemList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            OfferItem offerItem = new OfferItem();
            offerItem.setQuantity(RandomFactory.getRandomLong(5L));
            offerItem.setSelected(true);
            OfferId offerId = new OfferId();
            offerId.setValue(Master.getInstance().getOffer(offers.get(i)).getId());
            offerItem.setOffer(offerId);

            offerItemList.add(offerItem);
        }
        primaryCart.setOffers(offerItemList);
        primaryCart.setCoupons(couponItemList);

        Master.getInstance().addCart(primaryCartId, primaryCart);
        cartClient.updateCart(uid, primaryCartId, primaryCart);
    }

    public void postDefaultOffersToPrimaryCart(String uid) throws Exception {
        String offerId = this.postDefaultItem();
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offerId);
        this.postOffersToPrimaryCart(uid, offerList);
    }

    public void mergeCart(String destinationUid, String sourceUid) throws Exception {
        String sourceCartId = cartClient.getCartPrimary(sourceUid);
        Cart sourceCart = Master.getInstance().getCart(sourceCartId);

        String destinationCartId = cartClient.getCartPrimary(destinationUid);
        //Cart destinationCart = Master.getInstance().getCart(destinationCartId);

        cartClient.mergeCart(destinationUid, destinationCartId, sourceCart);
    }

}
