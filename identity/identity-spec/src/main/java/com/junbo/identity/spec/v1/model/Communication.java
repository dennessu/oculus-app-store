/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.CommunicationId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Communication extends ResourceMeta implements Identifiable<CommunicationId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The id of communication resource.")
    @JsonProperty("self")
    private CommunicationId id;

    @ApiModelProperty(position = 2, required = true, value = "The name of communication.")
    private String name;

    @ApiModelProperty(position = 3, required = true, value = "The description of communication.")
    private String description;

    @ApiModelProperty(position = 4, required = true, value = "An array of links to every locales the given communication is available in.")
    private List<LocaleId> translations = new ArrayList<>();

    @ApiModelProperty(position = 5, required = true, value = "An array of links to every countries the given communication is available in.")
    private List<CountryId> regions = new ArrayList<>();

    @ApiModelProperty(position = 6, required = true, value = "An array of mappings, the mapping is between a locale code and a JSON object contains " +
            "all the translation for this communication object in the locale.")
    private Map<String, JsonNode> locales = new HashMap<>();

    @ApiModelProperty(position = 7, required = true, value = " [non nullable, possibly empty] The future expansion of the communication resource.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

    public CommunicationId getId() {
        return id;
    }

    public void setId(CommunicationId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        support.setPropertyAssigned("name");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        support.setPropertyAssigned("description");
    }

    public List<CountryId> getRegions() {
        return regions;
    }

    public void setRegions(List<CountryId> regions) {
        this.regions = regions;
        support.setPropertyAssigned("regions");
    }

    public List<LocaleId> getTranslations() {
        return translations;
    }

    public void setTranslations(List<LocaleId> translations) {
        this.translations = translations;
        support.setPropertyAssigned("translations");
    }

    public Map<String, JsonNode> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, JsonNode> locales) {
        this.locales = locales;
        support.setPropertyAssigned("locales");
    }

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }
}
