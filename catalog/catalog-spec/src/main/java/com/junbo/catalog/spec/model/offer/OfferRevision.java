/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.*;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.jackson.annotation.*;
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
    private String revisionId;

    @JsonProperty("distributionChannel")
    @ApiModelProperty(position = 5, required = true, value = "An array of strings indicates where this item is capable of being sold",
            allowableValues = "INAPP, STORE")
    private List<String> distributionChannels;

    @ApiModelProperty(position = 6, required = true,
            value = "release notes for this version of the item, what has been changed, what bug has been fixed, this will show in the product page")
    private String revisionNotes;

    @JsonProperty("publisher")
    @ApiModelProperty(position = 20, required = true, value = "Organization owner of the offer revision resource")
    private OrganizationId ownerId;

    @OfferId
    @JsonProperty("offer")
    @ApiModelProperty(position = 21, required = true, value = "Offer of the offer revision")
    private String offerId;

    @ApiModelProperty(position = 22, required = true, value = "Offer price")
    private Price price;

    @ApiModelProperty(position = 23, required = true, value = "Offer restrictions")
    private Restriction restrictions;
    @OfferId
    @ApiModelProperty(position = 24, required = true, value = "Sub-offers")
    private List<String> subOffers;
    @ApiModelProperty(position = 25, required = true, value = "Items")
    private List<ItemEntry> items = new ArrayList<>();
    //@CountryId
    //@ApiModelProperty(position = 26, required = true, value = "Eligible countries")
    //private List<String> eligibleCountries;
    @ApiModelProperty(position = 27, required = true, value = "Event actions")
    private Map<String, List<Action>> eventActions;
    @ApiModelProperty(position = 28, required = true, value = "Start effective time [NOT AVAILABLE]")
    private Date startTime;
    @ApiModelProperty(position = 29, required = true, value = "End effective time [NOT AVAILABLE]")
    private Date endTime;

    @ApiModelProperty(position = 31, required = true, value = "Locale properties of the offer revision resource")
    private Map<String, OfferRevisionLocaleProperties> locales;
    @ApiModelProperty(position = 32, required = true,
            value = "The content ratings given to the offer by specific boards (ESRB, PEGI)")
    private Map<String, List<AgeRating>> ageRatings;
    @ApiModelProperty(position = 33, required = true, value = "Offer pre-order price")
    private Price preOrderPrice;
    @JsonProperty("regions")
    @ApiModelProperty(position = 34, required = true,
            value = "Maps from a country-code-name to a JSON object containing the country-specific properties")
    private Map<String, CountryProperties> countries;
    @ApiModelProperty(position = 35, required = true,
            value = "This is the calculated value to give how accurate the localizable attributes is.",
            allowableValues = "HIGH, MEDIUM, LOW")
    private String localeAccuracy;

    @ApiModelProperty(position = 36, required = true,
            value = "The display rank for the offer, used in sorting when showing a list of offers, expect to be float number")
    private Double rank;

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public List<String> getDistributionChannels() {
        return distributionChannels;
    }

    public void setDistributionChannels(List<String> distributionChannels) {
        this.distributionChannels = distributionChannels;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OrganizationId ownerId) {
        this.ownerId = ownerId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
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

    public List<String> getSubOffers() {
        return subOffers;
    }

    public void setSubOffers(List<String> subOffers) {
        this.subOffers = subOffers;
    }

    public List<ItemEntry> getItems() {
        return items;
    }

    public void setItems(List<ItemEntry> items) {
        this.items = items;
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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Map<String, OfferRevisionLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, OfferRevisionLocaleProperties> locales) {
        this.locales = locales;
    }

    public Map<String, List<AgeRating>> getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(Map<String, List<AgeRating>> ageRatings) {
        this.ageRatings = ageRatings;
    }

    public Price getPreOrderPrice() {
        return preOrderPrice;
    }

    public void setPreOrderPrice(Price preOrderPrice) {
        this.preOrderPrice = preOrderPrice;
    }

    public Map<String, CountryProperties> getCountries() {
        return countries;
    }

    public void setCountries(Map<String, CountryProperties> countries) {
        this.countries = countries;
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public String getRevisionNotes() {
        return revisionNotes;
    }

    public void setRevisionNotes(String revisionNotes) {
        this.revisionNotes = revisionNotes;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return revisionId;
    }

    @Override
    public void setId(String id) {
        this.revisionId = id;
    }

    @Override
    @JsonIgnore
    public String getEntityId() {
        return offerId;
    }
}
