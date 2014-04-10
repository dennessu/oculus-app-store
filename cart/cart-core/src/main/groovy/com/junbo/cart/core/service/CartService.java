/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.service;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.CartId;
import com.junbo.common.id.CartItemId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by fzhang@wan-san.com on 14-2-13.
 */
public interface CartService {

    Promise<Cart> addCart(Cart cart, String clientId, UserId userId);

    Promise<Cart> getCart(UserId userId, CartId cartId);

    Promise<Cart> getCartByName(String clientId, String cartName, UserId userId);

    Promise<Cart> getCartPrimary(String clientId, UserId userId);

    Promise<Cart> updateCart(UserId userId, CartId cartId, Cart cart);

    /**
     * @deprecated the merge logic does not needed anymore
     */
    @Deprecated
    Promise<Cart> mergeCart(UserId userId, CartId cartId, Cart fromCart);

    Promise<Cart> addOfferItem(UserId userId, CartId cartId, OfferItem offerItem);

    Promise<Cart> updateOfferItem(UserId userId, CartId cartId, CartItemId offerItemId, OfferItem offerItem);

    Promise<Cart> deleteOfferItem(UserId userId, CartId cartId, CartItemId offerItemId);

    Promise<Cart> addCouponItem(UserId userId, CartId cartId, CouponItem couponItem);

    Promise<Cart> deleteCouponItem(UserId userId, CartId cartId, CartItemId couponItemId);
}
