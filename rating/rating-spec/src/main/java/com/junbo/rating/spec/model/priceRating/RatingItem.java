/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.priceRating;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.PromotionRevisionId;
import com.junbo.common.jackson.annotation.ShippingMethodId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by lizwu on 2/12/14.
 */
public class RatingItem {
    @ApiModelProperty(position = 1, required = true, value = "The id of offer resource.")
    @OfferId
    @JsonProperty("offer")
    private String offerId;

    @ApiModelProperty(position = 2, required = true, value = "Quantity of offer.")
    private int quantity;

    @ApiModelProperty(position = 3, required = true, value = "[Client Immutable] The pre-order price of offer.")
    @Null
    private BigDecimal preOrderPrice;

    @ApiModelProperty(position = 4, required = true, value = "[Client Immutable] The original unit price of offer.")
    @Null
    private BigDecimal originalUnitPrice;

    @ApiModelProperty(position = 5, required = true, value = "[Client Immutable] The original total price of offer.")
    @Null
    private BigDecimal originalTotalPrice;

    @ApiModelProperty(position = 6, required = true, value = "[Client Immutable] The final total price of offer.")
    @Null
    private BigDecimal finalTotalAmount;

    @ApiModelProperty(position = 7, required = true, value = "[Client Immutable] Revenue split for developer.")
    @Null
    private BigDecimal developerRevenue;

    @ApiModelProperty(position = 8, required = true, value = "[Client Immutable] Total discount amount of offer.")
    @Null
    private BigDecimal totalDiscountAmount;

    @ApiModelProperty(position = 9, required = true,
            value = "[Client Immutable] Promotion rules which make discount for offer.")
    @Null
    @PromotionRevisionId
    private Set<String> promotions;

    @ApiModelProperty(position = 10, required = true, value = "Specify shipping method for offer.")
    @ShippingMethodId
    @JsonProperty("shippingMethod")
    private String shippingMethodId;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPreOrderPrice() {
        return preOrderPrice;
    }

    public void setPreOrderPrice(BigDecimal preOrderPrice) {
        this.preOrderPrice = preOrderPrice;
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

    public BigDecimal getDeveloperRevenue() {
        return developerRevenue;
    }

    public void setDeveloperRevenue(BigDecimal developerRevenue) {
        this.developerRevenue = developerRevenue;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public Set<String> getPromotions() {
        return promotions;
    }

    public void setPromotions(Set<String> promotions) {
        this.promotions = promotions;
    }

    public String getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(String shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }
}
