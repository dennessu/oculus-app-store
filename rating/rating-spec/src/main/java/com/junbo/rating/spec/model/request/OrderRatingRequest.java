/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.ShippingMethodId;
import com.junbo.common.jackson.annotation.UserId;

import javax.validation.constraints.Null;
import java.util.Set;

/**
 * Created by lizwu on 2/12/14.
 */
public class OrderRatingRequest {
    @UserId
    private Long userId;
    private String country;
    private String currency;

    private Set<String> couponCodes;

    private Set<OrderRatingItem> lineItems;

    @ShippingMethodId
    @JsonProperty("defaultShippingMethod")
    private Long shippingMethodId;

    @Null
    private OrderBenefit orderBenefit;

    @Null
    private ShippingBenefit shippingBenefit;

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

    public Set<String> getCouponCodes() {
        return couponCodes;
    }

    public void setCouponCodes(Set<String> couponCodes) {
        this.couponCodes = couponCodes;
    }

    public Set<OrderRatingItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<OrderRatingItem> lineItems) {
        this.lineItems = lineItems;
    }

    public Long getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(Long shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public OrderBenefit getOrderBenefit() {
        return orderBenefit;
    }

    public void setOrderBenefit(OrderBenefit orderBenefit) {
        this.orderBenefit = orderBenefit;
    }

    public ShippingBenefit getShippingBenefit() {
        return shippingBenefit;
    }

    public void setShippingBenefit(ShippingBenefit shippingBenefit) {
        this.shippingBenefit = shippingBenefit;
    }
}
