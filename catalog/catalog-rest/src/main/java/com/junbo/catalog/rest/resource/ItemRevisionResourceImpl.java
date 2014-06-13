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
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 * Item revision resource implementation.
 */
public class ItemRevisionResourceImpl implements ItemRevisionResource {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemAuthorizeCallbackFactory itemAuthorizeCallbackFactory;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public Promise<Results<ItemRevision>> getItemRevisions(ItemRevisionsGetOptions options) {
        List<ItemRevision> revisions = itemService.getRevisions(options);
        Results<ItemRevision> results = new Results<>();
        results.setItems(revisions);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        return Promise.pure(results);
    }

    private String buildNextUrl(ItemRevisionsGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getItemIds()) || !CollectionUtils.isEmpty(options.getRevisionIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("item-revisions");
        if (options.getStatus() != null) {
            builder.queryParam("status", options.getStatus().toUpperCase());
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
    public Promise<ItemRevision> getItemRevision(String revisionId) {
        return Promise.pure(itemService.getRevision(revisionId));
    }

    @Override
    public Promise<ItemRevision> createItemRevision(final ItemRevision itemRevision) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
            @Override
            public Promise<ItemRevision> apply() {

                if (!AuthorizeContext.hasRights("create")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(itemService.createRevision(itemRevision));
            }
        });
    }

    @Override
    public Promise<ItemRevision> updateItemRevision(final String revisionId, final ItemRevision itemRevision) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
            @Override
            public Promise<ItemRevision> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(itemService.updateRevision(revisionId, itemRevision));
            }
        });
    }

    @Override
    public Promise<Response> delete(final String revisionId) {
        ItemRevision itemRevision = itemService.getRevision(revisionId);
        if (itemRevision == null) {
            throw AppErrors.INSTANCE.notFound("item-revision", Utils.encodeId(revisionId)).exception();
        }

        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                itemService.deleteRevision(revisionId);
                return Promise.pure(Response.status(204).build());
            }
        });
    }
}
