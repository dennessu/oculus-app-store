/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.fusion;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by lizwu on 2/12/14.
 */
public class Price {
    public static final String FREE = "FREE";
    public static final String TIERED = "TIERED";
    public static final String CUSTOM = "CUSTOM";
    public static final Set<String> ALL_TYPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FREE, TIERED, CUSTOM)));

    private String priceType;
    private Map<String, BigDecimal> prices;

    public Price(String priceType, Map<String, BigDecimal> prices) {
        this.priceType = priceType;
        this.prices = prices;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public Map<String, BigDecimal> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, BigDecimal> prices) {
        this.prices = prices;
    }
}
