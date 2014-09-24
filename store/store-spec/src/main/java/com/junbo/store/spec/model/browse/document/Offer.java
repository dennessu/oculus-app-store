/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

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

    private Boolean isFree;

    private String formattedDescription;

    private CurrencyId currency;

    private OfferRevisionId currentRevision;

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
}
