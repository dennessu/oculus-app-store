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
public class Communication extends PropertyAssignedAwareResourceMeta implements Identifiable<CommunicationId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]he Link to communication resource.")
    @JsonProperty("self")
    private CommunicationId id;

    @ApiModelProperty(position = 4, required = true, value = "An array of Locale Links to the translations of the communication. " +
            "See 'locales' for the difference between 'locales' and 'translations'.")
    private List<LocaleId> translations = new ArrayList<>();

    @ApiModelProperty(position = 5, required = true, value = " An array of Country Links to the countries where the given communication is available.")
    private List<CountryId> regions = new ArrayList<>();

    @ApiModelProperty(position = 6, required = true, value = "A mapping from locale-code to a JSON object containing all the locale-specific descriptions about this communication. " +
            "Note: the 'locales' property provides localization of the communication's meta-data (e.g., for those who administrate or configure the communication)," +
            " whereas the 'translations' property lists the localizations of the communication body (e.g., for readers). " +
            "They are different, e.g., it might be possible to use French to administrate a Chinese communication.")
    private Map<String, JsonNode> locales = new HashMap<>();

    public CommunicationId getId() {
        return id;
    }

    public void setId(CommunicationId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
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
}
