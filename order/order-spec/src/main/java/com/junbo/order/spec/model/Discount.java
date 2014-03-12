/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.PromotionId;

import java.math.BigDecimal;

/**
 * Created by LinYi on 2/10/14.
 */
public class Discount extends BaseModelWithDate {
    @JsonIgnore
    private Long discountInfoId;
    @JsonIgnore
    private OrderId orderId;
    @JsonIgnore
    private OrderItemId orderItemId;
    private String type;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private PromotionId promotion;
    private String coupon;

    public Long getDiscountInfoId() {
        return discountInfoId;
    }

    public void setDiscountInfoId(Long discountInfoId) {
        this.discountInfoId = discountInfoId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getDiscountType() {
        return type;
    }

    public void setDiscountType(String discountType) {
        this.type = discountType;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public PromotionId getPromotion() {
        return promotion;
    }

    public void setPromotion(PromotionId promotion) {
        this.promotion = promotion;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
}
