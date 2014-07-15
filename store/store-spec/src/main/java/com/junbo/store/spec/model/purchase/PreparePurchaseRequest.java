/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

/**
 * The PreparePurchaseRequest class.
 */
public class PreparePurchaseRequest {
    private String offerId;
    private String country;
    private String locale;
    private String currency;
    private IAPParams iapParams;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public IAPParams getIapParams() {
        return iapParams;
    }

    public void setIapParams(IAPParams iapParams) {
        this.iapParams = iapParams;
    }
}
