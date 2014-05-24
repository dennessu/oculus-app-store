/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.common.jackson.annotation.ItemAttributeId;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Item Attribute.
 */
public class ItemAttribute extends BaseModel implements Attribute, Identifiable<Long> {
    @ItemAttributeId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "Attribute id")
    @JSONField(serialize = false, deserialize = false)
    private Long id;
    @NotNull
    @ApiModelProperty(position = 2, required = true, value = "Attribute type", allowableValues = "GENRE")
    private String type;
    @ItemAttributeId
    @JsonProperty("parent")
    @ApiModelProperty(position = 3, required = false, value = "Parent id")
    private Long parentId;
    @ApiModelProperty(position = 4, required = true, value = "Locale properties")
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
