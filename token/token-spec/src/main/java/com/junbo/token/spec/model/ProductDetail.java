/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.model;

import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.PromotionId;

import java.util.List;

/**
 * Product Type Detail.
 */
public class ProductDetail {
    @OfferId
    private String defaultOffer;
    @OfferId
    private List<String> optionalOffers;
    @PromotionId
    private String defaultPromotion;
    @PromotionId
    private List<String> optionalPromotion;

    public String getDefaultOffer() {
        return defaultOffer;
    }

    public void setDefaultOffer(String defaultOffer) {
        this.defaultOffer = defaultOffer;
    }

    public List<String> getOptionalOffers() {
        return optionalOffers;
    }

    public void setOptionalOffers(List<String> optionalOffers) {
        this.optionalOffers = optionalOffers;
    }


    public String getDefaultPromotion() {
        return defaultPromotion;
    }

    public void setDefaultPromotion(String defaultPromotion) {
        this.defaultPromotion = defaultPromotion;
    }

    public List<String> getOptionalPromotion() {
        return optionalPromotion;
    }

    public void setOptionalPromotion(List<String> optionalPromotion) {
        this.optionalPromotion = optionalPromotion;
    }

}
