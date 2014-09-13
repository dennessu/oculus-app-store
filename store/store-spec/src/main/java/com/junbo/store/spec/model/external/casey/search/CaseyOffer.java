/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.search;

import com.junbo.catalog.spec.model.offer.CountryProperties;
import com.junbo.common.id.OfferId;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.store.spec.model.external.casey.BaseCaseyModel;

import java.util.List;
import java.util.Map;

/**
 * The CaseyOffer class.
 */
public class CaseyOffer extends BaseCaseyModel {

    private OfferId self;
    private List<CatalogAttribute> categories;
    private Organization publisher;
    private List<CaseyItem> items;
    private CaseyPrice price;
    private Map<String, CountryProperties> countries;
    private String shortDescription;
    private String longDescription;

    public OfferId getSelf() {
        return self;
    }

    public void setSelf(OfferId self) {
        this.self = self;
    }

    public List<CatalogAttribute> getCategories() {
        return categories;
    }

    public void setCategories(List<CatalogAttribute> categories) {
        this.categories = categories;
    }

    public Organization getPublisher() {
        return publisher;
    }

    public void setPublisher(Organization publisher) {
        this.publisher = publisher;
    }

    public List<CaseyItem> getItems() {
        return items;
    }

    public void setItems(List<CaseyItem> items) {
        this.items = items;
    }

    public CaseyPrice getPrice() {
        return price;
    }

    public void setPrice(CaseyPrice price) {
        this.price = price;
    }

    public Map<String, CountryProperties> getCountries() {
        return countries;
    }

    public void setCountries(Map<String, CountryProperties> countries) {
        this.countries = countries;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
