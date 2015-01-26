/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.model;

import com.junbo.catalog.spec.model.common.Price;
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
    private String revisionId;
    private Organization owner;
    private ItemType type;
    private Price price;
    private Map<String, Date> countryReleaseDates;
    private Map<String, OfferLocale> locales;
    private List<ItemEntry> items;
    private Map<String, String> itemIds;
    private List<Offer> subOffers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
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

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
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

    public Map<String, String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Map<String, String> itemIds) {
        this.itemIds = itemIds;
    }

    public List<ItemEntry> getItems() {
        return items;
    }

    public void setItems(List<ItemEntry> items) {
        this.items = items;
    }

    public List<Offer> getSubOffers() {
        return subOffers;
    }

    public void setSubOffers(List<Offer> subOffers) {
        this.subOffers = subOffers;
    }
}
