/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.jackson.annotation.*;
import com.junbo.common.model.Link;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Item model.
 */
public class Item extends BaseEntityModel {
    @ItemId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The id of item resource")
    private String itemId;

    @ApiModelProperty(position = 2, required = true, value = "Item type", allowableValues =
            "PHYSICAL, APP, DOWNLOADED_ADDITION, STORED_VALUE, SUBSCRIPTION, PERMANENT_UNLOCK, CONSUMABLE_UNLOCK")
    private String type;

    @ItemRevisionId
    @JsonProperty("currentRevision")
    @ApiModelProperty(position = 20, required = true, value = "The id of current revision")
    private String currentRevisionId;

    @ApiModelProperty(position = 21, required = true, value = "Item revisions")
    @HateoasLink("/item-revisions?itemId={itemId}")
    private Link revisions;


    @JsonProperty("developer")
    @ApiModelProperty(position = 22, required = true, value = "Organization owner of the item")
    private OrganizationId ownerId;

    @ItemAttributeId
    @ApiModelProperty(position = 26, required = true, value = "Genres")
    private List<String> genres;

    @OfferId
    @ApiModelProperty(position = 28, required = true, value = "Default offer")
    private String defaultOffer;

    // current revision used for index & search
    @JsonIgnore
    private ItemRevision activeRevision;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getCurrentRevisionId() {
        return currentRevisionId;
    }

    @Override
    public void setCurrentRevisionId(String currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public Link getRevisions() {
        return revisions;
    }

    public void setRevisions(Link revisions) {
        this.revisions = revisions;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OrganizationId ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDefaultOffer() {
        return defaultOffer;
    }

    public void setDefaultOffer(String defaultOffer) {
        this.defaultOffer = defaultOffer;
    }

    public ItemRevision getActiveRevision() {
        return activeRevision;
    }

    public void setActiveRevision(ItemRevision activeRevision) {
        this.activeRevision = activeRevision;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return itemId;
    }

    @Override
    public void setId(String id) {
        this.itemId = id;
    }
}
