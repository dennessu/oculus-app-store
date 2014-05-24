/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;

import java.util.List;

/**
 * Item repository.
 */
public interface ItemRepository extends BaseEntityRepository<Item> {
    Item create(Item item);
    Item get(Long entityId);
    List<Item> getItems(ItemsGetOptions options);
    Item update(Item item);
    void delete(Long itemId);
}
