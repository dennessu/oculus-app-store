/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.mapper;

import com.junbo.common.id.*;

/**
 * Created by fzhang@wan-san.com on 14-2-15.
 */
@org.springframework.stereotype.Component("cartCommonMapper")
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
        return source == null ? null : new CartId(source.toString());
    }

    public Long fromCartIdToLong(CartId source) {
        return source == null ? null : Long.parseLong(source.getValue());
    }

    public CartItemId fromLongToCartItemId(Long source) {
        return source == null ? null : new CartItemId(source.toString());
    }

    public Long fromCartItemIdToLong(CartItemId source) {
        return source == null ? null : source.asLong();
    }

    public OfferId fromStringToOffer(String source) {
        return source == null ? null : new OfferId(source);
    }

    public String fromOfferToString(OfferId source) {
        return source == null ? null : source.getValue();
    }
}
