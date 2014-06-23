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
import com.junbo.common.enumid.RatingBoardId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Country extends PropertyAssignedAwareResourceMeta<CountryId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The Link of the country resource.")
    @JsonProperty("self")
    private CountryId id;

    @ApiModelProperty(position = 2, required = true,
            value = "[Nullable]The country code of the country resource, must be same as self.id.")
    private String countryCode;

    @ApiModelProperty(position = 3, required = true, value = "Link to the Locale resource that the country should use by default.")
    private LocaleId defaultLocale;

    @ApiModelProperty(position = 4, required = true, value = "Link to the Currency resource that the country should use by default.")
    private CurrencyId defaultCurrency;

    @ApiModelProperty(position = 5, required = true, value = "The array of Links to the AgeRatingBoards supported in the country.")
    private List<RatingBoardId> ratingBoards = new ArrayList<>();

    @ApiModelProperty(position = 6, required = true, value = "Not optional, not nullable, possibly empty, a JSON object " +
            "that maps from a code for the sub country (state, province, etc.) to a JSON object " +
            "that contains the localization keys for the sub country's short and long names.")
    private Map<String, SubCountryLocaleKeys> subCountries = new HashMap<>();

    @ApiModelProperty(position = 7, required = true, value = "Array for supported locale-Links to the locales appropriate for this country.")
    private List<LocaleId> supportedLocales = new ArrayList<>();

    @ApiModelProperty(position = 8, required = true, value = "Localizable properties and the corresponding Key value to lookup in Translation service.")
    private Map<String, CountryLocaleKey> locales;

    @ApiModelProperty(position = 9, required = false, value = "[Client Immutable] This is the calculated value to give how accurate the localizable attributes is. " +
            "The value must be in (HIGH, MEDIUM, LOW).  For a GET request without ?locale parameter, all localizable attributes are returned in all available locales, " +
            "the localAccuracy value should be \"HIGH\". In a GET request with ?locale parameter, " +
            "for example ?locale=\"es_ES\", if all the localizable attributes exist in the requested locale (\"es_ES\"), " +
            "the localAccuracy should be \"HIGH\". If partial of the localizable attributes are from requested locale (\"es_ES\"), " +
            "other part of the localizable attributes are from fallback locale, the localAccuracy value should be \"MEDIUM\". " +
            "If all of the localizable attributes are not from request locale (\"es_ES\"), all localizable attributes are from fall back locale, " +
            "the localAccracy value should be \"LOW\". ")
    private String localeAccuracy;

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

    public List<LocaleId> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(List<LocaleId> supportedLocales) {
        this.supportedLocales = supportedLocales;
        support.setPropertyAssigned("supportedLocales");
    }

    public Map<String, SubCountryLocaleKeys> getSubCountries() {
        return subCountries;
    }

    public void setSubCountries(Map<String, SubCountryLocaleKeys> subCountries) {
        this.subCountries = subCountries;
        support.setPropertyAssigned("subCountries");
    }

    public List<RatingBoardId> getRatingBoards() {
        return ratingBoards;
    }

    public void setRatingBoards(List<RatingBoardId> ratingBoards) {
        this.ratingBoards = ratingBoards;
        support.setPropertyAssigned("ratingBoards");
    }

    public Map<String, CountryLocaleKey> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, CountryLocaleKey> locales) {
        this.locales = locales;
        support.setPropertyAssigned("locales");
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
        support.setPropertyAssigned("localeAccuracy");
    }
}
