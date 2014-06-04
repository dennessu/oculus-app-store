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
    private String priceType;
    private Map<String, Map<String, BigDecimal>> prices;

    public Price(String priceType, Map<String, Map<String, BigDecimal>> prices) {
        this.priceType = priceType;
        this.prices = prices;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public Map<String, Map<String, BigDecimal>> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Map<String, BigDecimal>> prices) {
        this.prices = prices;
    }
}
