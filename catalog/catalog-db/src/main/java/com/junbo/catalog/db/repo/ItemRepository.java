/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;

import java.util.Collection;
import java.util.List;

/**
 * Item repository.
 */
public interface ItemRepository extends BaseEntityRepository<Item> {
    Item create(Item item);
    Item get(String entityId);
    List<Item> getItems(ItemsGetOptions options);
    List<Item> getItems(Collection<String> itemIds);
    Item update(Item item, Item oldItem);
    void delete(String itemId);
}
