/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;

import java.util.Map;

/**
 * Attribute.
 */
public interface Attribute {
    Long getId();
    void setId(Long id);
    String getType();
    void setType(String type);
    Long getParentId();
    void setParentId(Long parentId);
    Map<String, SimpleLocaleProperties> getLocales();
    void setLocales(Map<String, SimpleLocaleProperties> locales);
    Integer getResourceAge();
    void setResourceAge(Integer rev);
}
