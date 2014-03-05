/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.mapper;

import com.junbo.cart.spec.model.Coupon;
import com.junbo.cart.spec.model.Offer;
import com.junbo.common.id.CartId;
import com.junbo.common.id.CartItemId;
import com.junbo.common.id.UserId;

/**
 * Created by fzhang@wan-san.com on 14-2-15.
 */
@org.springframework.stereotype.Component
public class CommonMapper {

    public Long fromStringToLong(String source) {
        return source == null ? null : Long.parseLong(source);
    }

    public String fromLongToString(Long source) {
        return source == null ? null : source.toString();
    }

    public UserId fromLongToUserId(Long source) {
        return source == null ? null : new UserId(source);
    }

    public Long fromUserIdToLong(UserId source) {
        return source == null ? null : source.getValue();
    }

    public CartId fromLongToCartId(Long source) {
        return source == null ? null : new CartId(source);
    }

    public Long fromCartIdToLong(CartId source) {
        return source == null ? null : source.getValue();
    }

    public CartItemId fromLongToCartItemId(Long source) {
        return source == null ? null : new CartItemId(source);
    }

    public Long fromCartItemIdToLong(CartItemId source) {
        return source == null ? null : source.getValue();
    }

    public Offer fromLongToOffer(Long source) {
        return source == null ? null : new Offer(source);
    }

    public Long fromOfferToLong(Offer source) {
        return source == null ? null : source.getId();
    }

    public Coupon fromStringToCoupon(String source) {
        return source == null ? null : new Coupon(source);
    }

    public String fromCouponToString(Coupon source) {
        return source == null ? null : source.getId();
    }
}
