/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.junbo.common.jackson.annotation.PriceTierId;

import java.math.BigDecimal;
import java.util.*;

/**
 * Price.
 */
public class Price {
    public static final String FREE = "FREE";
    public static final String TIERED = "TIERED";
    public static final String CUSTOM = "CUSTOM";
    public static final Set<String> ALL_TYPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FREE, TIERED, CUSTOM)));

    private String priceType;
    @PriceTierId
    private Long priceTier;
    private Map<String, BigDecimal> prices;

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public Long getPriceTier() {
        return priceTier;
    }

    public void setPriceTier(Long priceTier) {
        this.priceTier = priceTier;
    }

    public Map<String, BigDecimal> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, BigDecimal> prices) {
        this.prices = prices;
    }
}
