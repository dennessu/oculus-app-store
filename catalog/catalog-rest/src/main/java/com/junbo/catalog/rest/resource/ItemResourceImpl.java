/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;

/**
 * Item resource implementation.
 */
public class ItemResourceImpl extends BaseResourceImpl<Item> implements ItemResource {
    @Autowired
    private ItemService itemService;

    @Override
    public Promise<ResultList<Item>> getItems(@BeanParam EntitiesGetOptions options) {
        return getEntities(options);
    }

    @Override
    public Promise<Item> getItem(Long itemId, @BeanParam EntityGetOptions options) {
        return get(itemId, options);
    }

    @Override
    protected ItemService getEntityService() {
        return itemService;
    }
}
