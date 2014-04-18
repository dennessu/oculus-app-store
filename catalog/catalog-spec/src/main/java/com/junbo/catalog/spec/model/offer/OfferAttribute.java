/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.common.jackson.annotation.OfferAttributeId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Offer Attribute.
 */
public class OfferAttribute extends BaseModel {
    @OfferAttributeId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "Attribute id")
    private Long id;
    @NotNull
    @ApiModelProperty(position = 2, required = true, value = "Attribute type", allowableValues = "CATEGORY")
    private String type;
    @OfferAttributeId
    @JsonProperty("parent")
    @ApiModelProperty(position = 1, required = true, value = "Parent id")
    private Long parentId;
    @ApiModelProperty(position = 3, required = false, value = "locale properties")
    private Map<String, SimpleLocaleProperties> locales;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Map<String, SimpleLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, SimpleLocaleProperties> locales) {
        this.locales = locales;
    }
}
