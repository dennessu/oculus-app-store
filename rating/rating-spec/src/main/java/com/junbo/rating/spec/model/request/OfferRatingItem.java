/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.PromotionId;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by lizwu on 2/12/14.
 */
public class OfferRatingItem {
    @OfferId
    @JsonProperty("offer")
    private Long offerId;

    @Null
    private BigDecimal originalAmount;

    @Null
    private BigDecimal discountAmount;

    @Null
    private BigDecimal finalAmount;

    @Null
    @PromotionId
    private Set<Long> promotions;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
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

    public Set<Long> getPromotions() {
        return promotions;
    }

    public void setPromotions(Set<Long> promotions) {
        this.promotions = promotions;
    }
}
