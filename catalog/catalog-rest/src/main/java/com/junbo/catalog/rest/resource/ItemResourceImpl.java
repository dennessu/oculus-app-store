/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.common.id.ItemId;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Item resource implementation.
 */
public class ItemResourceImpl extends BaseResourceImpl<Item> implements ItemResource {
    @Autowired
    private ItemService itemService;

    @Override
    public Promise<ResultList<Item>> getItems(ItemsGetOptions options) {
        return getEntities(options);
    }

    @Override
    public Promise<Item> getItem(ItemId itemId, EntityGetOptions options) {
        return get(itemId, options);
    }

    @Override
    public Promise<Item> update(ItemId itemId, Item item) {
        return super.update(itemId, item);
    }

    @Override
    protected ItemService getEntityService() {
        return itemService;
    }
}
