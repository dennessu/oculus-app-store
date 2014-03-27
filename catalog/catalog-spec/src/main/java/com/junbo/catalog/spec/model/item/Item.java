/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.VersionedModel;
import com.junbo.common.jackson.annotation.EntitlementDefinitionId;
import com.junbo.common.jackson.annotation.ItemId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.List;
import java.util.Map;

/**
 * Item model.
 */
public class Item extends VersionedModel {
    @ItemId
    @JsonProperty("self")
    private Long id;
    private String type;
    @UserId
    @JsonProperty("developer")
    private Long ownerId;
    private List<Sku> skus;
    private Map<String, Object> properties;

    @EntitlementDefinitionId
    @JsonProperty("entitlementDef")
    private Long entitlementDefId;

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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Long getEntitlementDefId() {
        return entitlementDefId;
    }

    public void setEntitlementDefId(Long entitlementDefId) {
        this.entitlementDefId = entitlementDefId;
    }

    @Override
    @JsonIgnore
    public String getEntityType() {
        return "Item";
    }
}
