/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.junbo.common.id.AttributeId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Attributes get options.
 */
public class AttributesGetOptions {
    // paging params
    @QueryParam("start")
    private Integer start;
    @QueryParam("size")
    private Integer size;

    // if entityIds is specified, paging params will be ignored.
    @QueryParam("id")
    private List<AttributeId> attributeIds;

    @QueryParam("type")
    private String attributeType;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<AttributeId> getAttributeIds() {
        return attributeIds;
    }

    public void setAttributeIds(List<AttributeId> attributeIds) {
        this.attributeIds = attributeIds;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
}
