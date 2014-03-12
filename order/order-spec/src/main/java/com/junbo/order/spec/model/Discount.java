/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.DiscountId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.jackson.annotation.PromotionId;

import java.math.BigDecimal;

/**
 * Created by LinYi on 2/10/14.
 */
public class Discount extends BaseModelWithDate {
    @JsonProperty("self")
    private DiscountId id;
    @JsonIgnore
    private OrderId order;
    private OrderItemId orderItem;
    private String type;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    @PromotionId
    private Long promotion;
    private String coupon;
    @JsonIgnore
    private Order ownerOrder;
    @JsonIgnore
    private OrderItem ownerOrderItem;

    public DiscountId getId() {
        return id;
    }

    public void setId(DiscountId id) {
        this.id = id;
    }

    public OrderId getOrder() {
        return order;
    }

    public void setOrder(OrderId order) {
        this.order = order;
    }

    public OrderItemId getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemId orderItem) {
        this.orderItem = orderItem;
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

    public Long getPromotion() {
        return promotion;
    }

    public void setPromotion(Long promotion) {
        this.promotion = promotion;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public Order getOwnerOrder() {
        return ownerOrder;
    }

    public void setOwnerOrder(Order ownerOrder) {
        this.ownerOrder = ownerOrder;
    }

    public OrderItem getOwnerOrderItem() {
        return ownerOrderItem;
    }

    public void setOwnerOrderItem(OrderItem ownerOrderItem) {
        this.ownerOrderItem = ownerOrderItem;
    }
}
