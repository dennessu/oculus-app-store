/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.catalog.spec.model.common.Interval;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.common.TypedProperties;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.OfferRevisionId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Offer revision.
 */
public class OfferRevision extends BaseRevisionModel {
    @OfferRevisionId
    @JsonProperty("self")
    private Long revisionId;

    @UserId
    @JsonProperty("publisher")
    private Long ownerId;

    @OfferId
    @JsonProperty("offer")
    private Long offerId;

    private Price price;

    private Restriction restrictions;
    @OfferId
    private List<Long> subOffers;
    private List<ItemEntry> items = new ArrayList<>();
    private List<String> eligibleCountries;
    private Map<String, Event> events;
    private TypedProperties<Interval> startEndTime;

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Restriction getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restriction restrictions) {
        this.restrictions = restrictions;
    }

    public List<Long> getSubOffers() {
        return subOffers;
    }

    public void setSubOffers(List<Long> subOffers) {
        this.subOffers = subOffers;
    }

    public List<ItemEntry> getItems() {
        return items;
    }

    public void setItems(List<ItemEntry> items) {
        this.items = items;
    }

    public List<String> getEligibleCountries() {
        return eligibleCountries;
    }

    public void setEligibleCountries(List<String> eligibleCountries) {
        this.eligibleCountries = eligibleCountries;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    public TypedProperties<Interval> getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(TypedProperties<Interval> startEndTime) {
        this.startEndTime = startEndTime;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return offerId;
    }
}
