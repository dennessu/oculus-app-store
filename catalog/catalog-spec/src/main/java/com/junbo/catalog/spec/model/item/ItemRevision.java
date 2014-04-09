/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.common.jackson.annotation.OfferId;

import java.util.Map;

/**
 * Item revision.
 */
public class ItemRevision extends BaseRevisionModel {
    @OfferId
    @JsonProperty("item")
    private Long itemId;
    private String type;
    private String sku;
    private Map<String, Map<String, Object>> localeProperties;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Map<String, Map<String, Object>> getLocaleProperties() {
        return localeProperties;
    }

    public void setLocaleProperties(Map<String, Map<String, Object>> localeProperties) {
        this.localeProperties = localeProperties;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return itemId;
    }
}
