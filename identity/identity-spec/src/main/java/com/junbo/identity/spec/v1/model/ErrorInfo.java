/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.cloudant.CloudantUnique;
import com.junbo.common.id.ErrorIdentifier;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * Created by liangfu on 7/22/14.
 */
public class ErrorInfo extends PropertyAssignedAwareResourceMeta<ErrorIdentifier> implements CloudantUnique {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The error id.")
    @JsonProperty("self")
    private ErrorIdentifier id;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = true, value = "The error identifier, it must be as the format as componentId.errorCode.")
    private String errorIdentifier;

    @ApiModelProperty(position = 3, required = true, value = "The localization of the error message.")
    private Map<String, JsonNode> locales;

    @ApiModelProperty(position = 4, required = false, value = "[Client Immutable] This is the calculated value to give how accurate the localizable attributes is. " +
            "The value must be in (HIGH, MEDIUM, LOW). For a GET request without ?locale parameter, all localizable attributes are returned in all available locales," +
            " the localAccuracy value should be \"HIGH\". In a GET request with ?locale parameter, for example ?locale=\"es_ES\", " +
            "if all the localizable attributes exist in the requested locale (\"es_ES\"), the localAccuracy should be \"HIGH\". " +
            "If partial of the localizable attributes are from requested locale (\"es_ES\"), other part of the localizable attributes are from fallback locale," +
            " the localAccuracy value should be \"MEDIUM\". If all of the localizable attributes are not from request locale (\"es_ES\")," +
            " all localizable attributes are from fall back locale, the localAccracy value should be \"LOW\".")
    private String localeAccuracy;

    @Override
    public ErrorIdentifier getId() {
        return id;
    }

    @Override
    public void setId(ErrorIdentifier id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getErrorIdentifier() {
        return errorIdentifier;
    }

    public void setErrorIdentifier(String errorIdentifier) {
        this.errorIdentifier = errorIdentifier;
        support.setPropertyAssigned("errorIdentifier");
    }

    public Map<String, JsonNode> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, JsonNode> locales) {
        this.locales = locales;
        support.setPropertyAssigned("locales");
    }

    @Override
    public String[] getUniqueKeys() {
        return new String[]{
                errorIdentifier
        };
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
        support.setPropertyAssigned("localeAccuracy");
    }
}
