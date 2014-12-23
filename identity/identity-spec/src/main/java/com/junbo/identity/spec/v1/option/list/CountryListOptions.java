/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.identity.spec.options.list.PagingGetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class CountryListOptions extends PagingGetOptions {
    // The below query parameters are used to search
    @QueryParam("defaultCurrencyId")
    private CurrencyId currencyId;

    @QueryParam("defaultLocaleId")
    private LocaleId localeId;

    @QueryParam("locale")
    private LocaleId returnLocale;

    @QueryParam("sortBy")
    private String sortBy;

    @QueryParam("properties")
    private String properties;

    public CurrencyId getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(CurrencyId currencyId) {
        this.currencyId = currencyId;
    }

    public LocaleId getLocaleId() {
        return localeId;
    }

    public void setLocaleId(LocaleId localeId) {
        this.localeId = localeId;
    }

    public LocaleId getReturnLocale() {
        return returnLocale;
    }

    public void setReturnLocale(LocaleId returnLocale) {
        this.returnLocale = returnLocale;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
