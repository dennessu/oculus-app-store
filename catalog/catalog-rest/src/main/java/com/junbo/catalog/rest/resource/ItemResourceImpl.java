/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.catalog.common.util.Utils;
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

import javax.ws.rs.core.Response;
import java.util.List;

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
        final Item item = itemService.getEntity(itemId.getValue());

        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return authorizeService.authorizeAndThen(callback, new Promise.Func0<Promise<Item>>() {
            @Override
            public Promise<Item> apply() {

                if (!AuthorizeContext.hasRights("read")) {
                    throw AppErrors.INSTANCE.notFound("Item", Utils.encodeId(item.getItemId())).exception();
                }

                return Promise.pure(item);
            }
        });
    }

    @Override
    public Promise<Item> update(final ItemId itemId, final Item item) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return authorizeService.authorizeAndThen(callback, new Promise.Func0<Promise<Item>>() {
            @Override
            public Promise<Item> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(itemService.updateEntity(itemId.getValue(), item));
            }
        });

    }

    @Override
    public Promise<Item> create(Item item) {
        return Promise.pure(itemService.createEntity(item));
    }

    @Override
    public Promise<Response> delete(ItemId itemId) {
        itemService.deleteEntity(itemId.getValue());
        return Promise.pure(Response.status(204).build());
    }
}
