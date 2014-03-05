/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by fzhang@wan-san.com on 14-1-21.
 */
@Entity
@Table(name = "CART_COUPON_ITEM")
@DynamicUpdate
public class CouponItemEntity extends CartItemEntity {

    private String couponCode;

    @Column(name = "COUPON_CODE")
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

}
