/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.CurrencyId;
import com.junbo.common.id.LocaleId;
import com.junbo.common.model.Link;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Currency extends ResourceMeta implements Identifiable<CurrencyId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of the currency resource.")
    @JsonProperty("self")
    private CurrencyId id;

    @ApiModelProperty(position = 2, required = true, value = "[Nullable]The currency code of the currency " +
            "resource must be same with ID.")
    private String currencyCode;

    @ApiModelProperty(position = 3, required = true, value = "The countries link of the currency resource.")
    private Link countries;

    @ApiModelProperty(position = 4, required = true, value = "The symbol of the currency resource.")
    private String symbol;

    @ApiModelProperty(position = 5, required = true, value = "The supported locales of the currency resource.")
    private Map<String, LocaleId> locales = new HashMap<>();

    @ApiModelProperty(position = 6, required = true, value = "The future expansion of the currency resource.")
    private Properties futureExpansion;

    public CurrencyId getId() {
        return id;
    }

    public void setId(CurrencyId id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Link getCountries() {
        return countries;
    }

    public void setCountries(Link countries) {
        this.countries = countries;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Map<String, LocaleId> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, LocaleId> locales) {
        this.locales = locales;
    }

    public Properties getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Properties futureExpansion) {
        this.futureExpansion = futureExpansion;
    }
}
