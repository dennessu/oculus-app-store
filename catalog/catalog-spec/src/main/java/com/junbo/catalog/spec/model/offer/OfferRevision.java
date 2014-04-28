/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.*;
import com.junbo.common.jackson.annotation.CountryId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.OfferRevisionId;
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

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
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The id of offer revision resource")
    private Long revisionId;

    @UserId
    @JsonProperty("publisher")
    @ApiModelProperty(position = 20, required = true, value = "Publisher of the offer revision resource")
    private Long ownerId;

    @OfferId
    @JsonProperty("offer")
    @ApiModelProperty(position = 21, required = true, value = "Offer of the offer revision")
    private Long offerId;

    @ApiModelProperty(position = 22, required = true, value = "Offer price")
    private Price price;

    @ApiModelProperty(position = 23, required = true, value = "Offer restrictions")
    private Restriction restrictions;
    @OfferId
    @ApiModelProperty(position = 24, required = true, value = "Sub-offers")
    private List<Long> subOffers;
    @ApiModelProperty(position = 25, required = true, value = "Items")
    private List<ItemEntry> items = new ArrayList<>();
    @CountryId
    @ApiModelProperty(position = 26, required = true, value = "Eligible countries")
    private List<String> eligibleCountries;
    @ApiModelProperty(position = 27, required = true, value = "Event actions")
    private Map<String, List<Action>> eventActions;
    @ApiModelProperty(position = 28, required = true, value = "Start effective time")
    private Date startTime;
    @ApiModelProperty(position = 29, required = true, value = "End effective time")
    private Date endTIme;

    @ApiModelProperty(position = 31, required = true, value = "Locale properties of the offer revision resource")
    private Map<String, OfferRevisionLocaleProperties> locales;

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

    public Map<String, List<Action>> getEventActions() {
        return eventActions;
    }

    public void setEventActions(Map<String, List<Action>> eventActions) {
        this.eventActions = eventActions;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTIme() {
        return endTIme;
    }

    public void setEndTIme(Date endTIme) {
        this.endTIme = endTIme;
    }

    public Map<String, OfferRevisionLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, OfferRevisionLocaleProperties> locales) {
        this.locales = locales;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return offerId;
    }
}
