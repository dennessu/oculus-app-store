/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model.item;

import com.junbo.cart.common.validate.Group;
import com.junbo.cart.spec.model.Coupon;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
public class CouponItem extends CartItem {

    @Valid
    @NotNull(groups = {Group.CartItem.class})
    private Coupon coupon;

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }
}
