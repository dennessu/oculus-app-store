/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrganizationId;

import java.util.Date;

/**
 * Created by acer on 2015/1/20.
 */
public class SubledgerKey {

    private CountryId country;
    private CurrencyId currency;
    private OrganizationId offerPublisher;
    private OfferId offerId;
    private Date subledgerTime;

    public CountryId getCountry() {
        return country;
    }

    public void setCountry(CountryId country) {
        this.country = country;
    }

    public CurrencyId getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyId currency) {
        this.currency = currency;
    }

    public OrganizationId getOfferPublisher() {
        return offerPublisher;
    }

    public void setOfferPublisher(OrganizationId offerPublisher) {
        this.offerPublisher = offerPublisher;
    }

    public OfferId getOfferId() {
        return offerId;
    }

    public void setOfferId(OfferId offerId) {
        this.offerId = offerId;
    }

    public Date getSubledgerTime() {
        return subledgerTime;
    }

    public void setSubledgerTime(Date subledgerTime) {
        this.subledgerTime = subledgerTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubledgerKey that = (SubledgerKey) o;

        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (offerId != null ? !offerId.equals(that.offerId) : that.offerId != null) return false;
        if (offerPublisher != null ? !offerPublisher.equals(that.offerPublisher) : that.offerPublisher != null)
            return false;
        if (subledgerTime != null ? !subledgerTime.equals(that.subledgerTime) : that.subledgerTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (offerPublisher != null ? offerPublisher.hashCode() : 0);
        result = 31 * result + (offerId != null ? offerId.hashCode() : 0);
        result = 31 * result + (subledgerTime != null ? subledgerTime.hashCode() : 0);
        return result;
    }
}
