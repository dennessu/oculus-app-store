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

    public Map<String, SubCountryLocaleKey> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, SubCountryLocaleKey> locales) {
        this.locales = locales;
    }
}
