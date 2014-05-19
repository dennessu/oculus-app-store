/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.enumid.RatingBoardId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Country extends PropertyAssignedAwareResourceMeta implements Identifiable<CountryId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The id of the country resource.")
    @JsonProperty("self")
    private CountryId id;

    @ApiModelProperty(position = 2, required = true,
            value = "[Nullable]The country code of the country resource, must be same with id. Client immutable; for query-convenience")
    private String countryCode;

    @ApiModelProperty(position = 3, required = true, value = "The default locale of the country resource.")
    private LocaleId defaultLocale;

    @ApiModelProperty(position = 4, required = true, value = "The default currency of the country resource.")
    private CurrencyId defaultCurrency;

    @ApiModelProperty(position = 5, required = true, value = "The array of Links to the ageRating Boards supported in the country.")
    private List<RatingBoardId> ratingBoardId = new ArrayList<>();

    @ApiModelProperty(position = 6, required = true, value = "Sub country object mapping.")
    private Map<String, SubCountry> subCountries = new HashMap<>();

    @ApiModelProperty(position = 7, required = true, value = "Array for supported locales in the country resource.")
    private List<LocaleId> supportedLocales = new ArrayList<>();

    @ApiModelProperty(position = 8, required = true, value = "Localizable properties")
    private Map<String, JsonNode> locales = new HashMap<>();

    @ApiModelProperty(position = 9, required = true, value = "The future expansion of the country resource.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

    public CountryId getId() {
        return id;
    }

    public void setId(CountryId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        support.setPropertyAssigned("countryCode");
    }

    public LocaleId getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(LocaleId defaultLocale) {
        this.defaultLocale = defaultLocale;
        support.setPropertyAssigned("defaultLocale");
    }

    public CurrencyId getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(CurrencyId defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
        support.setPropertyAssigned("defaultCurrency");
    }

    public List<RatingBoardId> getRatingBoardId() {
        return ratingBoardId;
    }

    public void setRatingBoardId(List<RatingBoardId> ratingBoardId) {
        this.ratingBoardId = ratingBoardId;
    }

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }

    public List<LocaleId> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(List<LocaleId> supportedLocales) {
        this.supportedLocales = supportedLocales;
        support.setPropertyAssigned("supportedLocales");
    }

    public Map<String, SubCountry> getSubCountries() {
        return subCountries;
    }

    public void setSubCountries(Map<String, SubCountry> subCountries) {
        this.subCountries = subCountries;
        support.setPropertyAssigned("subCountries");
    }

    public Map<String, JsonNode> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, JsonNode> locales) {
        this.locales = locales;
        support.setPropertyAssigned("locales");
    }
}
