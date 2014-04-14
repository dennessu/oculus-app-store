/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.*;

/**
 * Localizable Property.
 */
public class LocalizableProperty {
    private Map<String, String> locales = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> getLocales() {
        return locales;
    }

    // for fastjson
    public void setLocales(Map<String, String> locales) {
        this.locales = locales;
    }

    @JsonAnySetter
    public void set(String name, String value) {
        locales.put(name, value);
    }

    public String locale(String locale) {
        return locales == null ? null : locales.get(locale);
    }

    public static final String DEFAULT = "DEFAULT";
    public static final Set<String> LOCALES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            DEFAULT,
            "en_US",
            "zh_CN"
    )));
}
