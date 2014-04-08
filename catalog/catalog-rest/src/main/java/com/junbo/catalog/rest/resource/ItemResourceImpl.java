/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.common.id.ItemId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;

/**
 * Item resource implementation.
 */
public class ItemResourceImpl implements ItemResource {
    @Autowired
    private ItemService itemService;

    @Override
    public Promise<Results<Item>> getItems(ItemsGetOptions options) {
        return null;
    }

    @Override
    public Promise<Item> getItem(ItemId itemId, @BeanParam EntityGetOptions options) {
        return Promise.pure(itemService.getEntity(itemId.getValue()));
    }

    @Override
    public Promise<Item> update(ItemId itemId, Item item) {
        return Promise.pure(itemService.updateEntity(itemId.getValue(), item));
    }

    @Override
    public Promise<Item> create(Item item) {
        return Promise.pure(itemService.createEntity(item));
    }
}
