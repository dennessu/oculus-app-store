/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.fusion;

import com.junbo.common.id.OrganizationId;

import java.util.*;

/**
 * Created by lizwu on 1/28/14.
 */
public class RatingOffer {
    private String id;

    private Price price;
    private Price preOrderPrice;
    //private BigDecimal developerRatio;
    private OrganizationId organizationId;

    private Map<String, Properties> countries = new HashMap<>();

    private List<String> categories = new ArrayList<>();

    private List<LinkedEntry> items = new ArrayList<>();
    private List<LinkedEntry> subOffers = new ArrayList<>();

    private Map<String, List<OfferAction>> eventActions = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Price getPreOrderPrice() {
        return preOrderPrice;
    }

    public void setPreOrderPrice(Price preOrderPrice) {
        this.preOrderPrice = preOrderPrice;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(OrganizationId organizationId) {
        this.organizationId = organizationId;
    }

    public Map<String, Properties> getCountries() {
        return countries;
    }

    public void setCountries(Map<String, Properties> countries) {
        this.countries = countries;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
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
