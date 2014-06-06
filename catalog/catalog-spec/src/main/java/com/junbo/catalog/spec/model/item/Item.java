/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
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
    private Long itemId;

    @ApiModelProperty(position = 2, required = true, value = "Item type",
            allowableValues = "PHYSICAL, DIGITAL, STORED_VALUE, SUBSCRIPTION, VIRTUAL")
    private String type;

    @ItemRevisionId
    @JsonProperty("currentRevision")
    @ApiModelProperty(position = 20, required = true, value = "The id of current revision")
    private Long currentRevisionId;

    @ApiModelProperty(position = 21, required = true, value = "Item revisions")
    @HateoasLink("/item-revisions?itemId={itemId}")
    private Link revisions;

    @UserId
    @JsonProperty("developer")
    @ApiModelProperty(position = 22, required = true, value = "Developer of the item")
    private Long ownerId;

    @ItemAttributeId
    @ApiModelProperty(position = 26, required = true, value = "Genres")
    private List<Long> genres;

    @OfferId
    @ApiModelProperty(position = 28, required = true, value = "Default offer")
    private Long defaultOffer;

    // current revision used for index & search
    @JsonIgnore
    private ItemRevision activeRevision;

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

    @Override
    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    @Override
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

    public List<Long> getGenres() {
        return genres;
    }

    public void setGenres(List<Long> genres) {
        this.genres = genres;
    }

    public Long getDefaultOffer() {
        return defaultOffer;
    }

    public void setDefaultOffer(Long defaultOffer) {
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
    public Long getId() {
        return itemId;
    }

    @Override
    public void setId(Long id) {
        this.itemId = id;
    }
}
