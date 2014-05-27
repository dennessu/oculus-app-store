/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.catalog.auth.ItemAuthorizeCallbackFactory;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
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
        List<Item> items = itemService.getItems(options);

        final List<Item> filteredItems = new ArrayList<>();
        for (final Item item : items) {
            AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
            RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Void>() {
                @Override
                public Void apply() {
                    if (AuthorizeContext.hasRights("read")) {
                        filteredItems.add(item);
                    }

                    return null;
                }
            });
        }

        Results<Item> results = new Results<>();
        results.setItems(filteredItems);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);

        return Promise.pure(results);
    }

    private String buildNextUrl(ItemsGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getItemIds()) || options.getHostItemId() != null) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("items");
        if (options.getGenre() != null) {
            builder.queryParam("genre", IdFormatter.encodeId(options.getGenre()));
        }
        if (!StringUtils.isEmpty(options.getType())) {
            builder.queryParam("type", options.getType());
        }
        builder.queryParam("size", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextBookmark())) {
            builder.queryParam("bookmark", options.getNextBookmark());
        } else {
            builder.queryParam("start", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<Item> getItem(final ItemId itemId) {
        final Item item = itemService.getEntity(itemId.getValue());

        if (item == null) {
            throw AppErrors.INSTANCE.notFound("Item", IdFormatter.encodeId(itemId)).exception();
        }

        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Item>>() {
            @Override
            public Promise<Item> apply() {

                if (!AuthorizeContext.hasRights("read")) {
                    throw AppErrors.INSTANCE.notFound("Item", IdFormatter.encodeId(itemId)).exception();
                }

                return Promise.pure(item);
            }
        });
    }

    @Override
    public Promise<Item> update(final ItemId itemId, final Item item) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Item>>() {
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
    public Promise<Item> create(final Item item) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Item>>() {
            @Override
            public Promise<Item> apply() {

                if (!AuthorizeContext.hasRights("create")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(itemService.createEntity(item));
            }
        });
    }

    @Override
    public Promise<Response> delete(final ItemId itemId) {
        final Item item = itemService.getEntity(itemId.getValue());

        if (item == null) {
            throw AppErrors.INSTANCE.notFound("Item", IdFormatter.encodeId(itemId)).exception();
        }

        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                itemService.deleteEntity(itemId.getValue());
                return Promise.pure(Response.status(204).build());
            }
        });
    }
}
