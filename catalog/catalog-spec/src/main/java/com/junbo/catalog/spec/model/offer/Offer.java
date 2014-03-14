/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.VersionedModel;
import com.junbo.common.jackson.annotation.AttributeId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.List;
import java.util.Map;

/**
 * Offer model.
 */
public class Offer extends VersionedModel {
    @OfferId
    @JsonProperty("self")
    private Long id;

    @UserId
    private Long ownerId;

    @AttributeId
    private Long type;

    private Integer priceTier;
    private Map<String, Price> prices;

    private List<OfferEntry> subOffers;

    private List<ItemEntry> items;

    private Restriction restriction;

    @AttributeId
    private List<Long> categories;
    @AttributeId
    private List<Long> genres;

    private List<Event> events;
    private List<String> eligibleCountries;
    private Map<String, Map<String, String>> countryProperties;
    private Map<String, Map<String, String>> localeProperties;
    private Map<String, String> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Integer getPriceTier() {
        return priceTier;
    }

    public void setPriceTier(Integer priceTier) {
        this.priceTier = priceTier;
    }

    public Map<String, Price> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Price> prices) {
        this.prices = prices;
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

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public List<Long> getGenres() {
        return genres;
    }

    public void setGenres(List<Long> genres) {
        this.genres = genres;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<String> getEligibleCountries() {
        return eligibleCountries;
    }

    public void setEligibleCountries(List<String> eligibleCountries) {
        this.eligibleCountries = eligibleCountries;
    }

    public Map<String, Map<String, String>> getCountryProperties() {
        return countryProperties;
    }

    public void setCountryProperties(Map<String, Map<String, String>> countryProperties) {
        this.countryProperties = countryProperties;
    }

    public Map<String, Map<String, String>> getLocaleProperties() {
        return localeProperties;
    }

    public void setLocaleProperties(Map<String, Map<String, String>> localeProperties) {
        this.localeProperties = localeProperties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    @JsonIgnore
    public String getEntityType() {
        return "Offer";
    }
}
