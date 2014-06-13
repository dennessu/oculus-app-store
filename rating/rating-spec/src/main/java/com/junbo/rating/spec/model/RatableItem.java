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
    private String offerId;
    private int quantity;
    private String shippingMethodId;
    private RatingOffer offer;

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

    public String getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(String shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public RatingOffer getOffer() {
        return offer;
    }

    public void setOffer(RatingOffer offer) {
        this.offer = offer;
    }
}
