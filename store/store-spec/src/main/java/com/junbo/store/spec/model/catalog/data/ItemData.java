/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.catalog.data;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.identity.spec.v1.model.Organization;

import java.util.List;

/**
 * The ItemData class.
 */
public class ItemData {

    private OfferData offer;

    private Item item;
    private ItemRevision currentRevision;

    private List<ItemAttribute> genres;

    private Organization developer;

    private CaseyData caseyData;

    public OfferData getOffer() {
        return offer;
    }

    public void setOffer(OfferData offer) {
        this.offer = offer;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemRevision getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(ItemRevision currentRevision) {
        this.currentRevision = currentRevision;
    }

    public List<ItemAttribute> getGenres() {
        return genres;
    }

    public void setGenres(List<ItemAttribute> genres) {
        this.genres = genres;
    }

    public Organization getDeveloper() {
        return developer;
    }

    public void setDeveloper(Organization developer) {
        this.developer = developer;
    }

    public CaseyData getCaseyData() {
        return caseyData;
    }

    public void setCaseyData(CaseyData caseyData) {
        this.caseyData = caseyData;
    }
}
