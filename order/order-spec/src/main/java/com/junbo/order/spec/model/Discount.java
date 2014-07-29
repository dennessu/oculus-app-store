/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.PromotionId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by LinYi on 2/10/14.
 */
public class Discount extends ResourceMetaForDualWrite<Long> {
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private OrderId orderId;

    @JsonIgnore
    private OrderItemId orderItemId;

    @XSSFreeString
    @ApiModelProperty(required = true, position = 20, value = "[Client Immutable] The discount type. ",
            allowableValues = "OFFER_DISCOUNT, ORDER_DISCOUNT, SHIPPING_FEE_DISCOUNT. ")
    private String type;

    @ApiModelProperty(required = true, position = 30, value = "[Client Immutable] The discount amount.")
    private BigDecimal discountAmount;

    @ApiModelProperty(required = true, position = 40, value = "[Client Immutable] The discount percentage.")
    @JsonProperty("discountPercentage")
    private BigDecimal discountRate;

    @ApiModelProperty(required = true, position = 50, value = "[Client Immutable] The discount linked promotion.")
    private PromotionId promotion;

    @XSSFreeString
    @ApiModelProperty(required = false, position = 10, value = "The coupon linked discount.")
    private String coupon;

    @JsonIgnore
    private OrderItem ownerOrderItem;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public OrderItem getOwnerOrderItem() {
        return ownerOrderItem;
    }

    public void setOwnerOrderItem(OrderItem ownerOrderItem) {
        this.ownerOrderItem = ownerOrderItem;
    }
}
