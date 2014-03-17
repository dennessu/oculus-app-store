/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.common.id.ItemId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Items get options.
 */
public class ItemsGetOptions extends EntitiesGetOptions {
    @QueryParam("id")
    private List<ItemId> itemIds;

    public static ItemsGetOptions getDefault() {
        return setDefaults(new ItemsGetOptions());
    }

    public List<ItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<ItemId> itemIds) {
        this.itemIds = itemIds;
    }

    @Override
    public List<ItemId> getEntityIds() {
        return itemIds;
    }
}
