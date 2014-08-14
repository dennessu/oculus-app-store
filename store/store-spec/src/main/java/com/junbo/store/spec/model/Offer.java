/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.OfferId;

/**
 * The offer class for iap.
 */
public class Offer {

    private OfferId self;

    private String title;

    private String description;

    private String price;

    private CurrencyId currency;

    private String sku;

    public OfferId getSelf() {
        return self;
    }

    public void setSelf(OfferId self) {
        this.self = self;
    }

    public CurrencyId getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyId currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
