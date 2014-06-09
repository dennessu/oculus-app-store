/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.ItemAttributeId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OrganizationId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Items get options.
 */
public class ItemsGetOptions extends PageableGetOptions {
    @QueryParam("itemId")
    private Set<ItemId> itemIds;
    @QueryParam("type")
    private String type;
    @QueryParam("genreId")
    private ItemAttributeId genre;
    @QueryParam("hostItemId")
    private ItemId hostItemId;
    @QueryParam("ownerId")
    private OrganizationId ownerId;
    @QueryParam("q")
    private String query;

    public Set<ItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<ItemId> itemIds) {
        this.itemIds = itemIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItemAttributeId getGenre() {
        return genre;
    }

    public void setGenre(ItemAttributeId genre) {
        this.genre = genre;
    }

    public ItemId getHostItemId() {
        return hostItemId;
    }

    public void setHostItemId(ItemId hostItemId) {
        this.hostItemId = hostItemId;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OrganizationId ownerId) {
        this.ownerId = ownerId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
