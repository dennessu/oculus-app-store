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

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import java.util.List;

/**
 * Item resource implementation.
 */
public class ItemResourceImpl implements ItemResource {
    @Autowired
    private ItemService itemService;

    @Override
    public Promise<ResultList<Item>> getItems(EntitiesGetOptions options) {
        List<Item> items = itemService.getItems(options);
        ResultList<Item> resultList = new ResultList<>();
        resultList.setResults(items);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }

    @Override
    public Promise<Item> getItem(Long itemId, @BeanParam EntityGetOptions options) {
        return Promise.pure(itemService.getItem(itemId, options));
    }

    @Override
    public Promise<Item> createItem(@Valid Item item) {
        return Promise.pure(itemService.createItem(item));
    }

    @Override
    public Promise<Item> updateItem(@Valid Item item) {
        return Promise.pure(itemService.updateItem(item));
    }

    @Override
    public Promise<Item> createReview(Long itemId) {
        return Promise.pure(itemService.reviewItem(itemId));
    }

    @Override
    public Promise<Item> releaseItem(Long itemId) {
        return Promise.pure(itemService.releaseItem(itemId));
    }

    @Override
    public Promise<Item> rejectItem(Long itemId) {
        return Promise.pure(itemService.rejectItem(itemId));
    }

    @Override
    public Promise<Long> removeItem(Long itemId) {
        return Promise.pure(itemService.removeItem(itemId));
    }

    @Override
    public Promise<Long> deleteItem(Long itemId) {
        return Promise.pure(itemService.deleteItem(itemId));
    }
}
