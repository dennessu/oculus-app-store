/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.model;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.order.spec.model.enums.ItemType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The subset of catalog offer model needed by Order component.
 */
public class Offer {
    private String id;
    private Organization owner;
    private ItemType type;
    private Map<String, Date> countryReleaseDates;
    private Map<String, OfferLocale> locales;
    private List<Item> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Organization getOwner() {
        return owner;
    }

    public void setOwner(Organization owner) {
        this.owner = owner;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public Map<String, Date> getCountryReleaseDates() {
        return countryReleaseDates;
    }

    public void setCountryReleaseDates(Map<String, Date> countryReleaseDates) {
        this.countryReleaseDates = countryReleaseDates;
    }

    public Map<String, OfferLocale> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, OfferLocale> locales) {
        this.locales = locales;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
