/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.TosId;
import com.junbo.common.jackson.annotation.XSSFreeRichText;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class Tos extends PropertyAssignedAwareResourceMeta<TosId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]Link to the TOS resource.")
    @JsonProperty("self")
    private TosId id;

    @ApiModelProperty(position = 2, required = true, value = "The type of the TOS, must be from an enum list (EULA, TOS, PP, etc).")
    private String type;

    @ApiModelProperty(position = 3, required = true, value = "Array of links to Country resource, represents which countries this TOS is good in.")
    private List<CountryId> countries = new ArrayList<>();

    @XSSFreeString
    @ApiModelProperty(position = 4, required = true, value = "Version number of the TOS.")
    private String version;

    @XSSFreeRichText
    @ApiModelProperty(position = 6, required = true, value = "The content of the TOS resource.")
    private String content;

    @ApiModelProperty(position = 7, required = true, value = "State of the TOS, it must in [DRAFT, APPROVED, OBSOLETE].")
    private String state;

    @ApiModelProperty(position = 8, required = false, value = "Array of links to Locale resource, represents which locale this TOS is good in.")
    private List<LocaleId> coveredLocales;

    @ApiModelProperty(position = 9, required = false, value = "")
    private Map<String, TosLocaleProperty> locales;

    @ApiModelProperty(position = 10, required = false, value = "minor version number for TOS typo fix, this expect to not trigger TOS re-challenge")
    private Double minorversion;

    @Override
    public TosId getId() {
        return id;
    }

    public void setId(TosId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        support.setPropertyAssigned("content");
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        support.setPropertyAssigned("state");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public List<CountryId> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryId> countries) {
        this.countries = countries;
        support.setPropertyAssigned("countries");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        support.setPropertyAssigned("version");
    }

    public List<LocaleId> getCoveredLocales() {
        return coveredLocales;
    }

    public void setCoveredLocales(List<LocaleId> coveredLocales) {
        this.coveredLocales = coveredLocales;
        support.setPropertyAssigned("coveredLocales");
    }

    public Map<String, TosLocaleProperty> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, TosLocaleProperty> locales) {
        this.locales = locales;
        support.setPropertyAssigned("locales");
    }

    public Double getMinorversion() {
        return minorversion;
    }

    public void setMinorversion(Double minorversion) {
        this.minorversion = minorversion;
        support.setPropertyAssigned("minorversion");
    }
}
