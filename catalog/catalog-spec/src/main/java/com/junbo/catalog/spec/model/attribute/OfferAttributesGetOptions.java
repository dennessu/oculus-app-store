/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.OfferAttributeId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Attributes get options.
 */
public class OfferAttributesGetOptions extends PageableGetOptions {
    @QueryParam("id")
    private List<OfferAttributeId> attributeIds;

    @QueryParam("type")
    private String attributeType;

    public List<OfferAttributeId> getAttributeIds() {
        return attributeIds;
    }

    public void setAttributeIds(List<OfferAttributeId> attributeIds) {
        this.attributeIds = attributeIds;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
}
