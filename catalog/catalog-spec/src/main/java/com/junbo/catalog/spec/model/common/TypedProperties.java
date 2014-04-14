/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

/**
 * Typed Extensible Properties.
 * @param <T> type
 */
public class TypedProperties<T> {
    private Map<String, T> properties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, T> getProperties() {
        return properties;
    }

    // for fastjson
    public void setProperties(Map<String, T> properties) {
        this.properties = properties;
    }

    @JsonAnySetter
    public void set(String name, T value) {
        properties.put(name, value);
    }

    public T get(String name) {
        return properties == null ? null : properties.get(name);
    }
}
