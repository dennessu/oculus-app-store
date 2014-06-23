/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangfu on 6/12/14.
 */
public class SubCountryLocaleKeys {
    @ApiModelProperty(position = 1, required = false, value = "Locale keys map.")
    private Map<String, SubCountryLocaleKey> locales = new HashMap<>();

    @ApiModelProperty(position = 2, required = false, value = " [Client Immutable] This is the calculated value to give how accurate the localizable attributes is. " +
            "The value must be in (HIGH, MEDIUM, LOW). For a GET request without ?locale parameter, all localizable attributes are returned in all available locales," +
            " the localAccuracy value should be \"HIGH\". In a GET request with ?locale parameter, for example ?locale=\"es_ES\", " +
            "if all the localizable attributes exist in the requested locale (\"es_ES\"), the localAccuracy should be \"HIGH\". " +
            "If partial of the localizable attributes are from requested locale (\"es_ES\"), other part of the localizable attributes are from fallback locale, " +
            "the localAccuracy value should be \"MEDIUM\". If all of the localizable attributes are not from request locale (\"es_ES\"), " +
            "all localizable attributes are from fall back locale, the localAccracy value should be \"LOW\".")
    private String localeAccuracy;

    public Map<String, SubCountryLocaleKey> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, SubCountryLocaleKey> locales) {
        this.locales = locales;
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
    }
}
