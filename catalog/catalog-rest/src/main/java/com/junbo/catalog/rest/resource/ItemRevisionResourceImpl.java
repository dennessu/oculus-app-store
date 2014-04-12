/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Item revision resource implementation.
 */
public class ItemRevisionResourceImpl implements ItemRevisionResource {
    @Autowired
    private ItemService itemService;

    @Override
    public Promise<Results<ItemRevision>> getItemRevisions(ItemId itemId) {
        return null;
    }

    @Override
    public Promise<ItemRevision> getItemRevision(ItemRevisionId revisionId) {
        return Promise.pure(itemService.getRevision(revisionId.getValue()));
    }

    @Override
    public Promise<ItemRevision> createItemRevision(ItemRevision itemRevision) {
        return Promise.pure(itemService.createRevision(itemRevision));
    }

    @Override
    public Promise<ItemRevision> updateItemRevision(ItemRevisionId revisionId, ItemRevision itemRevision) {
        return Promise.pure(itemService.updateRevision(revisionId.getValue(), itemRevision));
    }
}
