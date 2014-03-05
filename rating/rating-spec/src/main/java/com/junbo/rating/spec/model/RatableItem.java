/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model;

import com.junbo.rating.spec.fusion.RatingOffer;

/**
 * Created by lizwu on 2/6/14.
 */
public class RatableItem {
    private Long offerId;
    private int quantity;
    private RatingOffer offer;

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

    public RatingOffer getOffer() {
        return offer;
    }

    public void setOffer(RatingOffer offer) {
        this.offer = offer;
    }
}
