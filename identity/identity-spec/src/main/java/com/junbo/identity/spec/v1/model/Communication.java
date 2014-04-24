/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.CommunicationId;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Communication extends ResourceMeta implements Identifiable<CommunicationId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable]The id of communication resource.")
    @JsonProperty("self")
    private CommunicationId id;

    @ApiModelProperty(position = 2, required = true, value = "The name of communication.")
    private String name;

    @ApiModelProperty(position = 3, required = true, value = "The description of communication.")
    private String description;

    @ApiModelProperty(position = 4, required = true, value = "Indicated which country a communication is allowed in.")
    private CountryId allowedIn;

    @ApiModelProperty(position = 5, required = true, value = "For each language a given communication is available in.")
    private List<LocaleId> locales = new ArrayList<>();

    @ApiModelProperty(position = 6, required = false, value = "Available regions.")
    private List<String> regions = new ArrayList<>();

    @ApiModelProperty(position = 7, required = false, value = "Available translations")
    private List<String> translations = new ArrayList<>();

    public CommunicationId getId() {
        return id;
    }

    public void setId(CommunicationId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CountryId getAllowedIn() {
        return allowedIn;
    }

    public void setAllowedIn(CountryId allowedIn) {
        this.allowedIn = allowedIn;
    }

    public List<LocaleId> getLocales() {
        return locales;
    }

    public void setLocales(List<LocaleId> locales) {
        this.locales = locales;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<String> getTranslations() {
        return translations;
    }

    public void setTranslations(List<String> translations) {
        this.translations = translations;
    }
}
