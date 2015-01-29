/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OrganizationId;
import com.junbo.order.spec.model.enums.SubledgerType;

import java.util.Date;

/**
 * Created by acer on 2015/1/20.
 */
public class SubledgerCriteria {

    private CountryId country;
    private CurrencyId currency;
    private String subledgerKey;
    private SubledgerType subledgerType;
    private OrganizationId seller;
    private ItemId itemId;
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

    public OrganizationId getSeller() {
        return seller;
    }

    public void setSeller(OrganizationId seller) {
        this.seller = seller;
    }

    public String getSubledgerKey() {
        return subledgerKey;
    }

    public void setSubledgerKey(String subledgerKey) {
        this.subledgerKey = subledgerKey;
    }

    public SubledgerType getSubledgerType() {
        return subledgerType;
    }

    public void setSubledgerType(SubledgerType subledgerType) {
        this.subledgerType = subledgerType;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
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

        SubledgerCriteria that = (SubledgerCriteria) o;

        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) return false;
        if (seller != null ? !seller.equals(that.seller) : that.seller != null) return false;
        if (subledgerKey != null ? !subledgerKey.equals(that.subledgerKey) : that.subledgerKey != null) return false;
        if (subledgerTime != null ? !subledgerTime.equals(that.subledgerTime) : that.subledgerTime != null)
            return false;
        if (subledgerType != that.subledgerType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (subledgerKey != null ? subledgerKey.hashCode() : 0);
        result = 31 * result + (subledgerType != null ? subledgerType.hashCode() : 0);
        result = 31 * result + (seller != null ? seller.hashCode() : 0);
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        result = 31 * result + (subledgerTime != null ? subledgerTime.hashCode() : 0);
        return result;
    }
}
