/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;

import java.util.Map;

/**
 * Attribute.
 */
public interface Attribute {
    String getId();
    void setId(String id);
    String getRev();
    String getType();
    void setType(String type);
    String getParentId();
    void setParentId(String parentId);
    Map<String, SimpleLocaleProperties> getLocales();
    void setLocales(Map<String, SimpleLocaleProperties> locales);
    Map<String, JsonNode> getFutureExpansion();
}
