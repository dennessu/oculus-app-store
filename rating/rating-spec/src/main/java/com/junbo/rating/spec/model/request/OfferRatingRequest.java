/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.CountryId;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.Set;

/**
 * Created by lizwu on 2/12/14.
 */
public class OfferRatingRequest {
    @UserId
    @JsonProperty("user")
    private Long userId;

    @CountryId
    private String country;

    @CurrencyId
    private String currency;

    private Set<OfferRatingItem> offers;
    private String time;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Set<OfferRatingItem> getOffers() {
        return offers;
    }

    public void setOffers(Set<OfferRatingItem> offers) {
        this.offers = offers;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
