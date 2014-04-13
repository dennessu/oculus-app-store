/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.LocalizableProperty;
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
    private LocalizableProperty name;
    @NotNull
    private String type;
    private LocalizableProperty description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalizableProperty getName() {
        return name;
    }

    public void setName(LocalizableProperty name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalizableProperty getDescription() {
        return description;
    }

    public void setDescription(LocalizableProperty description) {
        this.description = description;
    }
}
