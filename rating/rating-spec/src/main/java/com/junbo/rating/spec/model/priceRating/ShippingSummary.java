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
 * Created by lizwu on 2/24/14.
 */
public class ShippingSummary {
    @ApiModelProperty(position = 1, required = true,
            value = "[Client Immutable] Promotion rule which makes discount for shipping fee.")
    @PromotionRevisionId
    private Long promotion;

    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable] Shipping fee for order.")
    private BigDecimal totalShippingFee;

    public Long getPromotion() {
        return promotion;
    }

    public void setPromotion(Long promotion) {
        this.promotion = promotion;
    }

    public BigDecimal getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(BigDecimal totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }
}
