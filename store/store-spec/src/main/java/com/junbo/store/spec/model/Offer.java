/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;

import java.util.Map;

/**
 * The offer class for iap.
 */
public class Offer {
    private String offerId;
    private Map<String, OfferRevisionLocaleProperties> offerLocales;
    private Price price;
    private Boolean isConsumable;
    private String type;
    private String sku;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public Map<String, OfferRevisionLocaleProperties> getOfferLocales() {
        return offerLocales;
    }

    public void setOfferLocales(Map<String, OfferRevisionLocaleProperties> offerLocales) {
        this.offerLocales = offerLocales;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Boolean getIsConsumable() {
        return isConsumable;
    }

    public void setIsConsumable(Boolean isConsumable) {
        this.isConsumable = isConsumable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
