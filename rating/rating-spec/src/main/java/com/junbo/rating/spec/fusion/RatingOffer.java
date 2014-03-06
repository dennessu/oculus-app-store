/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.fusion;

import java.util.*;

/**
 * Created by lizwu on 1/28/14.
 */
public class RatingOffer {
    private Long id;

    private Map<String, Price> prices;
    private Set<Long> categories;

    private List<LinkedEntry> items;
    private List<LinkedEntry> subOffers;

    public RatingOffer() {
        prices = new HashMap<String, Price>();
        categories = new HashSet<Long>();
        items = new ArrayList<LinkedEntry>();
        subOffers = new ArrayList<LinkedEntry>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Price> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Price> prices) {
        this.prices = prices;
    }

    public Set<Long> getCategories() {
        return categories;
    }

    public void setCategories(Set<Long> categories) {
        this.categories = categories;
    }

    public List<LinkedEntry> getItems() {
        return items;
    }

    public void setItems(List<LinkedEntry> items) {
        this.items = items;
    }

    public List<LinkedEntry> getSubOffers() {
        return subOffers;
    }

    public void setSubOffers(List<LinkedEntry> subOffers) {
        this.subOffers = subOffers;
    }
}
