/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.model.AuthorizeContext;
import com.junbo.authorization.service.AuthorizeService;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.rest.auth.ItemAuthorizeCallbackFactory;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.common.id.ItemId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Item resource implementation.
 */
public class ItemResourceImpl implements ItemResource {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemAuthorizeCallbackFactory itemAuthorizeCallbackFactory;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public Promise<Results<Item>> getItems(ItemsGetOptions options) {
        List<Item> offers = itemService.getItems(options);
        Results<Item> results = new Results<>();
        results.setItems(offers);
        return Promise.pure(results);
    }

    @Override
    public Promise<Item> getItem(ItemId itemId) {
        Item item = itemService.getEntity(itemId.getValue());

        Map<String, Object> context = new HashMap<>();
        context.put("apiName", "item_get");
        context.put("entity", item);

        AuthorizeCallback callback = itemAuthorizeCallbackFactory.create(context);
        authorizeService.authorize(callback);

        if (!AuthorizeContext.hasRight("read")) {
            throw AppErrors.INSTANCE.notFound("Item", item.getItemId()).exception();
        }

        return Promise.pure(item);
    }

    @Override
    public Promise<Item> update(ItemId itemId, Item item) {
        Map<String, Object> context = new HashMap<>();
        context.put("apiName", "item_update");
        context.put("entity", item);

        AuthorizeCallback callback = itemAuthorizeCallbackFactory.create(context);
        authorizeService.authorize(callback);

        if (!AuthorizeContext.hasRight("update")) {
            throw AppErrors.INSTANCE.accessDenied().exception();
        }

        return Promise.pure(itemService.updateEntity(itemId.getValue(), item));
    }

    @Override
    public Promise<Item> create(Item item) {
        return Promise.pure(itemService.createEntity(item));
    }
}
