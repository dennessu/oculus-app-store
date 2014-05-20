/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.ItemAttributeId;
import com.junbo.common.id.ItemId;

import javax.ws.rs.QueryParam;
import java.util.Collection;

/**
 * Items get options.
 */
public class ItemsGetOptions extends PageableGetOptions {
    @QueryParam("itemId")
    private Collection<ItemId> itemIds;
    @QueryParam("type")
    private String type;
    @QueryParam("genre")
    private ItemAttributeId genre;
    @QueryParam("hostItemId")
    private ItemId hostItemId;

    public Collection<ItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Collection<ItemId> itemIds) {
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
}
