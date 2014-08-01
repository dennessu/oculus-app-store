/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Locale extends PropertyAssignedAwareResourceMeta<LocaleId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] Link to the locale resource.")
    @JsonProperty("self")
    private LocaleId id;

    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable] The locale code of the locale,  use en_US as standard, " +
            "but also accept en-US in the GET request.")
    private String localeCode;

    @XSSFreeString
    @ApiModelProperty(position = 3, required = false, value = "[Nullable]The short name of the locale.")
    private String shortName;

    @XSSFreeString
    @ApiModelProperty(position = 4, required = false, value = "[Nullable]The long name of the locale.")
    private String longName;

    @XSSFreeString
    @ApiModelProperty(position = 5, required = false, value = "[Nullable]The locale name of the locale.")
    private String localeName;

    @ApiModelProperty(position = 6, required = false, value = "[Nullable] Link to the fallback locale.")
    private LocaleId fallbackLocale;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        support.setPropertyAssigned("shortName");
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
        support.setPropertyAssigned("longName");
    }

    public LocaleId getId() {
        return id;
    }

    public void setId(LocaleId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
        support.setPropertyAssigned("localeCode");
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
        support.setPropertyAssigned("localeName");
    }

    public LocaleId getFallbackLocale() {
        return fallbackLocale;
    }

    public void setFallbackLocale(LocaleId fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
        support.setPropertyAssigned("fallbackLocale");
    }
}
