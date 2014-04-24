/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Item revision resource implementation.
 */
public class ItemRevisionResourceImpl implements ItemRevisionResource {
    @Autowired
    private ItemService itemService;

    @Override
    public Promise<Results<ItemRevision>> getItemRevisions(ItemRevisionsGetOptions options) {
        List<ItemRevision> revisions = itemService.getRevisions(options);
        Results<ItemRevision> results = new Results<>();
        results.setItems(revisions);
        return Promise.pure(results);
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

    @Override
    public Promise<Response> delete(ItemRevisionId revisionId) {
        itemService.deleteRevision(revisionId.getValue());
        return Promise.pure(Response.status(204).build());
    }
}
