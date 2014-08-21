/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;

import javax.ws.rs.QueryParam;

/**
 * The ListRequest class.
 */
public class ListRequest {

    @QueryParam("locale")
    private LocaleId locale;

    @QueryParam("country")
    private CountryId country;

    @QueryParam("category")
    private String category;

    @QueryParam("criteria")
    private String criteria;

    @QueryParam("cursor")
    private Integer cursor;

    @QueryParam("count")
    private Integer count;

    public LocaleId getLocale() {
        return locale;
    }

    public void setLocale(LocaleId locale) {
        this.locale = locale;
    }

    public CountryId getCountry() {
        return country;
    }

    public void setCountry(CountryId country) {
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Integer getCursor() {
        return cursor;
    }

    public void setCursor(Integer cursor) {
        this.cursor = cursor;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
