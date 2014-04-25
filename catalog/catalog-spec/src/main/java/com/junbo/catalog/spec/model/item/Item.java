/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.catalog.spec.model.common.Link;
import com.junbo.common.jackson.annotation.*;
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

    @ApiModelProperty(position = 2, required = true, value = "Item type",
            allowableValues = "PHYSICAL, DIGITAL, WALLET, SUBSCRIPTION")
    private String type;

    @ItemRevisionId
    @JsonProperty("currentRevision")
    @ApiModelProperty(position = 20, required = true, value = "The id of current revision")
    private Long currentRevisionId;

    @ApiModelProperty(position = 21, required = true, value = "Item revisions")
    private Link revisions;

    @UserId
    @JsonProperty("developer")
    @ApiModelProperty(position = 22, required = true, value = "Developer of the item")
    private Long ownerId;

    @ApiModelProperty(position = 23, required = true,
            value = "An ID that helps to group like items. ex. TEAM_FORTRESS, this rollup ID would be applied to" +
                    "all items that are team fortress (PC, MAC, LINUX, etc)")
    private String rollupPackageName;

    @ApiModelProperty(position = 24, required = true,
            value = "Used to identify the item (app), used mainly for android")
    private String packageName;

    @ApiModelProperty(position = 25, required = true, value = "The platform name, for digital goods only",
            allowableValues = "PC, MAC, LINUX, ANDROID")
    private List<String> platforms;

    @ItemAttributeId
    @ApiModelProperty(position = 26, required = true, value = "Genres")
    private List<Long> genres;

    @ApiModelProperty(position = 27, required = true, value = "Sku")
    private String sku;

    @JsonIgnore
    private Long entitlementDefId;

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

    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(Long currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public Link getRevisions() {
        return revisions;
    }

    public void setRevisions(Link revisions) {
        this.revisions = revisions;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getRollupPackageName() {
        return rollupPackageName;
    }

    public void setRollupPackageName(String rollupPackageName) {
        this.rollupPackageName = rollupPackageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
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
