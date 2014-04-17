/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.common.jackson.annotation.AttributeId;
import com.junbo.common.jackson.annotation.ItemId;
import com.junbo.common.jackson.annotation.ItemRevisionId;
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Item model.
 */
public class Item extends BaseEntityModel {
    @ItemId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The id of item resource")
    private Long itemId;

    @ApiModelProperty(position = 2, required = true, value = "Item type")
    private String type;

    @ItemRevisionId
    @JsonProperty("currentRevision")
    @ApiModelProperty(position = 20, required = true, value = "The id of current revision")
    private Long currentRevisionId;

    @UserId
    @JsonProperty("developer")
    @ApiModelProperty(position = 21, required = true, value = "Developer of the item")
    private Long ownerId;

    @AttributeId
    @ApiModelProperty(position = 22, required = true, value = "Genres")
    private List<Long> genres;

    @ApiModelProperty(position = 23, required = true, value = "Sku")
    private String sku;

    @JsonIgnore
    private Long entitlementDefId;

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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    public Long getEntitlementDefId() {
        return entitlementDefId;
    }

    public void setEntitlementDefId(Long entitlementDefId) {
        this.entitlementDefId = entitlementDefId;
    }
}
