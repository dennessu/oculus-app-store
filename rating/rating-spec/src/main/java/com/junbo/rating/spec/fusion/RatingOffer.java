/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.fusion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lizwu on 1/28/14.
 */
public class RatingOffer {
    private Long id;

    private Map<String, RatingPrice> prices;
    private Set<Long> categories;

    public RatingOffer() {
        prices = new HashMap<String, RatingPrice>();
        categories = new HashSet<Long>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, RatingPrice> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, RatingPrice> prices) {
        this.prices = prices;
    }

    public Set<Long> getCategories() {
        return categories;
    }

    public void setCategories(Set<Long> categories) {
        this.categories = categories;
    }
}
