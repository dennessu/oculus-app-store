/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.PromotionId;
import com.junbo.common.jackson.annotation.ShippingMethodId;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by lizwu on 2/12/14.
 */
public class OrderRatingItem {
    @OfferId
    @JsonProperty("Offer")
    private Long offerId;

    private int quantity;

    @Null
    private BigDecimal originalUnitPrice;

    @Null
    private BigDecimal originalTotalPrice;

    @Null
    private BigDecimal finalTotalAmount;

    @Null
    private BigDecimal totalDiscountAmount;

    @Null
    @PromotionId
    private Set<Long> promotions;

    @ShippingMethodId
    @JsonProperty("ShippingMethod")
    private Long shippingMethodId;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(BigDecimal originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public BigDecimal getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public void setOriginalTotalPrice(BigDecimal originalTotalPrice) {
        this.originalTotalPrice = originalTotalPrice;
    }

    public BigDecimal getFinalTotalAmount() {
        return finalTotalAmount;
    }

    public void setFinalTotalAmount(BigDecimal finalTotalAmount) {
        this.finalTotalAmount = finalTotalAmount;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public Set<Long> getPromotions() {
        return promotions;
    }

    public void setPromotions(Set<Long> promotions) {
        this.promotions = promotions;
    }

    public Long getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(Long shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }
}
