/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.junbo.catalog.spec.model.common.PageableGetOptions;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Attributes get options.
 */
public class OfferAttributesGetOptions extends PageableGetOptions {
    @QueryParam("attributeId")
    private Set<String> attributeIds;
    @QueryParam("type")
    private String attributeType;
    @QueryParam("locale")
    private String locale;

    public Set<String> getAttributeIds() {
        return attributeIds;
    }

    public void setAttributeIds(Set<String> attributeIds) {
        this.attributeIds = attributeIds;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
