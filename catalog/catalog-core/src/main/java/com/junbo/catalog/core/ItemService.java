/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Item service definition.
 */
@Transactional
public interface ItemService extends BaseRevisionedService<Item, ItemRevision> {
    List<Item> getItems(ItemsGetOptions options);
    List<ItemRevision> getRevisions(ItemRevisionsGetOptions options);
}
