/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.BaseModel;

import java.util.List;
import java.util.Map;

/**
 * Offer revision.
 */
public class OfferRevision extends BaseModel {
    private Long offerId;
    private String status;
    private String priceType;
    private Long priceTierId;
    private Map<String, Price> prices;
    private Restriction restriction;
    private List<String> eligibleCountries;
    private Map<String, Event> events;
    private Map<String, Map<String, Object>> countryProperties;
    private Map<String, Map<String, Object>> localeProperties;
    private Map<String, Object> properties;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Map<String, Map<String, Object>> getCountryProperties() {
        return countryProperties;
    }

    public void setCountryProperties(Map<String, Map<String, Object>> countryProperties) {
        this.countryProperties = countryProperties;
    }

    public Map<String, Map<String, Object>> getLocaleProperties() {
        return localeProperties;
    }

    public void setLocaleProperties(Map<String, Map<String, Object>> localeProperties) {
        this.localeProperties = localeProperties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
