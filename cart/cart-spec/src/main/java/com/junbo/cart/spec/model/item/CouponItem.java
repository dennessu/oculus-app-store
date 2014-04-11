/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model.item;

import com.junbo.cart.common.validate.Group;
import com.junbo.common.id.CouponId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
public class CouponItem extends CartItem {

    @ApiModelProperty(required = true, position = 1, value = "The coupon code.")
    @Valid
    @NotNull(groups = {Group.CartItem.class})
    private CouponId coupon;

    public CouponId getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponId coupon) {
        this.coupon = coupon;
    }
}
