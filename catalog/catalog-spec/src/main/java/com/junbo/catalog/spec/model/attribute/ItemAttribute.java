/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.attribute;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.common.jackson.annotation.ItemAttributeId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Item Attribute.
 */
public class ItemAttribute extends BaseModel implements Attribute {
    @ItemAttributeId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "Attribute id")
    @JSONField(serialize = false, deserialize = false)
    private String id;

    @NotNull
    @ApiModelProperty(position = 2, required = true, value = "Attribute type", allowableValues = "GENRE")
    private String type;

    @ItemAttributeId
    @JsonProperty("parent")
    @ApiModelProperty(position = 3, required = false, value = "Parent id")
    private String parentId;

    @ApiModelProperty(position = 4, required = true, value = "Locale properties")
    private Map<String, SimpleLocaleProperties> locales;

    @ApiModelProperty(position = 5, required = true,
            value = "This is the calculated value to give how accurate the localizable attributes is.",
            allowableValues = "HIGH, MEDIUM, LOW")
    private String localeAccuracy;

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Map<String, SimpleLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, SimpleLocaleProperties> locales) {
        this.locales = locales;
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
    }
}
