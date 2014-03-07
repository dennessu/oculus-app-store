/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.common.jackson.annotation.CategoryId;

import javax.validation.constraints.Null;
import java.util.Map;

/**
 * Category model.
 */
public class Category extends BaseModel {
    @Null
    @CategoryId
    @JsonProperty("self")
    private Long id;

    @CategoryId
    private Long parentId;

    private Map<String, String> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String getEntityType() {
        return "Category";
    }
}
