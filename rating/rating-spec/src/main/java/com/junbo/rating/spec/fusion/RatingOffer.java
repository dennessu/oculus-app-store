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

    private Price price;
    private List<Long> categories = new ArrayList<>();

    private List<LinkedEntry> items = new ArrayList<>();
    private List<LinkedEntry> subOffers = new ArrayList<>();

    private Map<String, List<OfferAction>> eventActions = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
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

    public Map<String, List<OfferAction>> getEventActions() {
        return eventActions;
    }

    public void setEventActions(Map<String, List<OfferAction>> eventActions) {
        this.eventActions = eventActions;
    }
}
