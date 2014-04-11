/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.catalog.spec.model.common.TypedProperties;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.OfferRevisionId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.ArrayList;
import java.util.Date;
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
    private String priceType;
    private Long priceTierId;
    private Map<String, Price> prices;
    private Restriction restriction;
    private List<OfferEntry> subOffers;
    private List<ItemEntry> items = new ArrayList<>();
    private List<String> eligibleCountries;
    private Map<String, Event> events;
    private TypedProperties<Date> startTime;
    private TypedProperties<Date> endTime;

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

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public Long getPriceTierId() {
        return priceTierId;
    }

    public void setPriceTierId(Long priceTierId) {
        this.priceTierId = priceTierId;
    }

    public Map<String, Price> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Price> prices) {
        this.prices = prices;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public List<OfferEntry> getSubOffers() {
        return subOffers;
    }

    public void setSubOffers(List<OfferEntry> subOffers) {
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

    public TypedProperties<Date> getStartTime() {
        return startTime;
    }

    public void setStartTime(TypedProperties<Date> startTime) {
        this.startTime = startTime;
    }

    public TypedProperties<Date> getEndTime() {
        return endTime;
    }

    public void setEndTime(TypedProperties<Date> endTime) {
        this.endTime = endTime;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return offerId;
    }
}
