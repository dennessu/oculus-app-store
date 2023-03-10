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
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.common.error.AppCommonErrors;
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

        Results<Item> results = new Results<>();
        results.setItems(items);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        results.setTotal(options.getTotal());

        return Promise.pure(results);
    }

    private String buildNextUrl(ItemsGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getItemIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("items");
        if (!StringUtils.isEmpty(options.getGenre())) {
            builder.queryParam("genre", options.getGenre());
        }
        if (!StringUtils.isEmpty(options.getType())) {
            builder.queryParam("type", options.getType());
        }
        if (options.getOwnerId() != null) {
            builder.queryParam("developerId", IdFormatter.encodeId(options.getOwnerId()));
        }
        if (options.getHostItemId() != null) {
            builder.queryParam("hostItemId", options.getHostItemId());
        }
        builder.queryParam("count", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextCursor())) {
            builder.queryParam("cursor", options.getNextCursor());
        } else {
            builder.queryParam("cursor", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<Item> getItem(final String itemId) {
        final Item item = itemService.getEntity(itemId);

        if (item == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("Item", itemId).exception();
        }

        return Promise.pure(item);
    }

    @Override
    public Promise<Item> update(final String itemId, final Item item) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Item>>() {
            @Override
            public Promise<Item> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(itemService.updateEntity(itemId, item));
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
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(itemService.createEntity(item));
            }
        });
    }

    @Override
    public Promise<Response> delete(final String itemId) {
        final Item item = itemService.getEntity(itemId);

        if (item == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("Item", itemId).exception();
        }

        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(item);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                itemService.deleteEntity(itemId);
                return Promise.pure(Response.status(204).build());
            }
        });
    }
}
