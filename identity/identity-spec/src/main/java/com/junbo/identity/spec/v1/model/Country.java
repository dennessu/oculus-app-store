/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.*;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Country extends ResourceMeta implements Identifiable<CountryId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of the country resource.")
    @JsonProperty("self")
    private CountryId id;

    @ApiModelProperty(position = 2, required = true,
            value = "[Nullable]The country code of the country resource, must be same with id. Client immutable; for query-convenience")
    private String countryCode;

    @ApiModelProperty(position = 3, required = true, value = "The default locale of the country resource.")
    private LocaleId defaultLocale;

    @ApiModelProperty(position = 4, required = true, value = "The default currency of the country resource.")
    private CurrencyId defaultCurrency;

    @ApiModelProperty(position = 5, required = true, value = "The future expansion of the country resource.")
    private Properties futureExpansion;

    @ApiModelProperty(position = 6, required = true, value = "Array for supported locales in the country resource.")
    private List<LocaleId> supportedLocales = new ArrayList<>();

    @ApiModelProperty(position = 7, required = true, value = "Sub country object mapping.")
    private Map<String, SubCountry> subCountries = new HashMap<>();

    @ApiModelProperty(position = 8, required = false, value = "")
    private Map<String, LocaleKey> localekeys = new HashMap<>();

    public CountryId getId() {
        return id;
    }

    public void setId(CountryId id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public LocaleId getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(LocaleId defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public CurrencyId getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(CurrencyId defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Properties getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Properties futureExpansion) {
        this.futureExpansion = futureExpansion;
    }

    public List<LocaleId> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(List<LocaleId> supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public Map<String, SubCountry> getSubCountries() {
        return subCountries;
    }

    public void setSubCountries(Map<String, SubCountry> subCountries) {
        this.subCountries = subCountries;
    }

    public Map<String, LocaleKey> getLocalekeys() {
        return localekeys;
    }

    public void setLocalekeys(Map<String, LocaleKey> localekeys) {
        this.localekeys = localekeys;
    }
}
