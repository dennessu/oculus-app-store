/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.search;

import com.junbo.catalog.spec.model.common.Images;
import com.junbo.catalog.spec.model.offer.CountryProperties;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.id.OrganizationId;

import java.util.List;
import java.util.Map;

/**
 * The CaseyOffer class.
 */
public class CaseyOffer {

    private OfferId self;
    private List<CatalogAttribute> categories;
    private OrganizationId publisher;
    private List<CaseyItem> items;
    private CaseyPrice price;
    private Map<String, CountryProperties> regions;
    private String name;
    private String shortDescription;
    private String longDescription;
    private OfferRevisionId currentRevision;
    private Images images;

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

    public OrganizationId getPublisher() {
        return publisher;
    }

    public void setPublisher(OrganizationId publisher) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, CountryProperties> getRegions() {
        return regions;
    }

    public void setRegions(Map<String, CountryProperties> regions) {
        this.regions = regions;
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

    public OfferRevisionId getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(OfferRevisionId currentRevision) {
        this.currentRevision = currentRevision;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}
