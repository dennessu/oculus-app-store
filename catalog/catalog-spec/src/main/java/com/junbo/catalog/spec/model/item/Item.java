/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.common.jackson.annotation.AppId;
import com.junbo.common.jackson.annotation.ItemId;

import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

/**
 * Item model.
 */
public class Item extends BaseModel {
    @Null
    @ItemId
    @JsonProperty("self")
    private Long id;
    private String type;
    @AppId
    private Long ownerId;
    private List<Sku> skus;
    private Map<String, String> properties;

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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String getEntityType() {
        return "Item";
    }
}
