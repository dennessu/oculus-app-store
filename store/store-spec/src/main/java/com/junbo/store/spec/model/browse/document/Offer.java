/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OfferRevisionId;

import java.math.BigDecimal;

/**
 * The Offer class.
 */
public class Offer {

    private OfferId self;

    private BigDecimal price;

    private String formattedPrice;

    private Boolean isFree;

    private String formattedDescription;

    private CurrencyId currency;

    private OfferRevisionId currentRevision;

    @JsonIgnore
    private Boolean hasStoreValueItem;

    @JsonIgnore
    private Boolean hasPhysicalItem;

    public OfferId getSelf() {
        return self;
    }

    public void setSelf(OfferId self) {
        this.self = self;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public String getFormattedDescription() {
        return formattedDescription;
    }

    public void setFormattedDescription(String formattedDescription) {
        this.formattedDescription = formattedDescription;
    }

    public CurrencyId getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyId currency) {
        this.currency = currency;
    }

    public OfferRevisionId getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(OfferRevisionId currentRevision) {
        this.currentRevision = currentRevision;
    }

    public Boolean getHasStoreValueItem() {
        return hasStoreValueItem;
    }

    public void setHasStoreValueItem(Boolean hasStoreValueItem) {
        this.hasStoreValueItem = hasStoreValueItem;
    }

    public Boolean getHasPhysicalItem() {
        return hasPhysicalItem;
    }

    public void setHasPhysicalItem(Boolean hasPhysicalItem) {
        this.hasPhysicalItem = hasPhysicalItem;
    }
}
