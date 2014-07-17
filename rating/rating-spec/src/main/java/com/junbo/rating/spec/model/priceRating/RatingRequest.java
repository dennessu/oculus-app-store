/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.priceRating;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.CountryId;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.ShippingMethodId;
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lizwu on 2/12/14.
 */
public class RatingRequest {
    @ApiModelProperty(position = 1, required = true, value = "represent whether request is from order service.")
    @NotNull
    private Boolean includeCrossOfferPromos;

    @ApiModelProperty(position = 2, required = true, value = "The id of user resource.")
    @UserId
    @JsonProperty("user")
    private Long userId;

    @ApiModelProperty(position = 3, required = true, value = "Country.")
    @NotNull
    @CountryId
    private String country;

    @ApiModelProperty(position = 4, required = true, value = "CurrencyInfo code.")
    @NotNull
    @CurrencyId
    private String currency;

    @ApiModelProperty(position = 5, required = true, value = "Coupon codes.")
    private Set<String> coupons = new HashSet<>();

    @ApiModelProperty(position = 6, required = true, value = "Line items to be rated.")
    private Set<RatingItem> lineItems;

    @ApiModelProperty(position = 7, required = true, value = "Get offers by specific timestamp.")
    private String time;

    @ApiModelProperty(position = 8, required = true,
            value = "Default shipping method " +
                    "for physical goods which have no specified shipping method.")
    @ShippingMethodId
    @JsonProperty("defaultShippingMethod")
    private String shippingMethodId;

    @ApiModelProperty(position = 9, required = true, value = "[Client Immutable] Details about calculation result of an order.")
    @Null
    private RatingSummary ratingSummary;

    @ApiModelProperty(position = 10, required = true, value = "[Client Immutable] Details about calculation result of shipping fee.")
    @Null
    private ShippingSummary shippingSummary;

    public Boolean getIncludeCrossOfferPromos() {
        return includeCrossOfferPromos;
    }

    public void setIncludeCrossOfferPromos(Boolean includeCrossOfferPromos) {
        this.includeCrossOfferPromos = includeCrossOfferPromos;
    }

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

    public Set<String> getCoupons() {
        return coupons;
    }

    public void setCoupons(Set<String> coupons) {
        this.coupons = coupons;
    }

    public Set<RatingItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<RatingItem> lineItems) {
        this.lineItems = lineItems;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(String shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public RatingSummary getRatingSummary() {
        return ratingSummary;
    }

    public void setRatingSummary(RatingSummary ratingSummary) {
        this.ratingSummary = ratingSummary;
    }

    public ShippingSummary getShippingSummary() {
        return shippingSummary;
    }

    public void setShippingSummary(ShippingSummary shippingSummary) {
        this.shippingSummary = shippingSummary;
    }
}
