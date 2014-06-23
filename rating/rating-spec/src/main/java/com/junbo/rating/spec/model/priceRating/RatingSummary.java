/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.priceRating;

import com.junbo.common.jackson.annotation.PromotionRevisionId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by lizwu on 2/12/14.
 */
public class RatingSummary {
    @ApiModelProperty(position = 1, required = true,
            value = "[Client Immutable] Promotion rule which makes discount for order.")
    @PromotionRevisionId
    private String promotion;

    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable] Discount amount of order.")
    private BigDecimal discountAmount;

    @ApiModelProperty(position = 3, required = true, value = "[Client Immutable] Final Price of the whole order.")
    private BigDecimal finalAmount;

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
}
