/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.common.jackson.annotation.PriceTierId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.*;

/**
 * Price.
 */
public class Price {
    @ApiModelProperty(position = 1, required = true, value = "price type", allowableValues = "TIERED, CUSTOM, FREE")
    private String priceType;

    @PriceTierId
    @ApiModelProperty(position = 2, required = true, value = "price tier")
    private String priceTier;

    @ApiModelProperty(position = 3, required = true, value = "prices")
    private Map<String, Map<String, BigDecimal>> prices;

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPriceTier() {
        return priceTier;
    }

    public void setPriceTier(String priceTier) {
        this.priceTier = priceTier;
    }

    public Map<String, Map<String, BigDecimal>> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Map<String, BigDecimal>> prices) {
        this.prices = prices;
    }
}
