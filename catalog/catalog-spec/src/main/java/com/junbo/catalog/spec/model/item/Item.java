/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.common.jackson.annotation.AttributeId;
import com.junbo.common.jackson.annotation.ItemId;
import com.junbo.common.jackson.annotation.ItemRevisionId;

import java.util.List;

/**
 * Item model.
 */
public class Item extends BaseEntityModel {
    @ItemId
    @JsonProperty("self")
    private Long itemId;

    @ItemRevisionId
    @JsonProperty("currentRevision")
    private Long currentRevisionId;

    private String type;

    @AttributeId
    private List<Long> genres;

    private String sku;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(Long currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Long> getGenres() {
        return genres;
    }

    public void setGenres(List<Long> genres) {
        this.genres = genres;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
