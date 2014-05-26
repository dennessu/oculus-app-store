/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.ItemAttributeId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Attributes get options.
 */
public class ItemAttributesGetOptions extends PageableGetOptions {
    @QueryParam("attributeId")
    private Set<ItemAttributeId> attributeIds;

    @QueryParam("type")
    private String attributeType;

    public Set<ItemAttributeId> getAttributeIds() {
        return attributeIds;
    }

    public void setAttributeIds(Set<ItemAttributeId> attributeIds) {
        this.attributeIds = attributeIds;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
}
