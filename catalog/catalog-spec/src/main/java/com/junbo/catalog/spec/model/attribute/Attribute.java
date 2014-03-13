/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.common.jackson.annotation.AttributeId;

import javax.validation.constraints.NotNull;

/**
 * Attribute.
 */
public class Attribute extends BaseModel {
    @AttributeId
    @JsonProperty("self")
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
