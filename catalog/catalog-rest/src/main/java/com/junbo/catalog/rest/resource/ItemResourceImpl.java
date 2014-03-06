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
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Item resource implementation.
 */
public class ItemResourceImpl implements ItemResource {
    @Autowired
    private ItemService itemService;

    @Override
    public Promise<ResultList<Item>> getItems(EntitiesGetOptions options) {
        List<Item> items;
        if (options.getEntityIds() != null && options.getEntityIds().size() > 0) {
            items = new ArrayList<>();
            for (Long itemId : options.getEntityIds()) {
                items.add(itemService.getItem(itemId, EntityGetOptions.getDefault()));
            }
        } else {
            options.ensurePagingValid();
            items = itemService.getItems(options.getStart(), options.getSize());
        }
        ResultList<Item> resultList = new ResultList<>();
        resultList.setResults(items);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }

    @Override
    public Promise<Item> getItem(Long itemId, EntityGetOptions options) {
        return Promise.pure(itemService.getItem(itemId, options));
    }

    @Override
    public Promise<Item> createItem(Item item) {
        return Promise.pure(itemService.createItem(item));
    }

    @Override
    public Promise<Item> createReview(Long itemId) {
        Item item = itemService.getItem(itemId, EntityGetOptions.getDefault());
        item.setStatus(Status.PENDING_REVIEW);
        // save the updated item
        return Promise.pure(item);
    }

    @Override
    public Promise<Item> publishItem(Long itemId) {
        Item item = itemService.getItem(itemId, EntityGetOptions.getDefault());
        item.setStatus(Status.RELEASED);
        // save the updated item
        return Promise.pure(item);
    }
}
