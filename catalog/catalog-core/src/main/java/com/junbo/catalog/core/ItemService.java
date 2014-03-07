/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Item service definition.
 */
@Transactional
public interface ItemService {
    Item getItem(Long itemId, EntityGetOptions options);
    List<Item> getItems(EntitiesGetOptions options);
    Item createItem(Item item);
    Item updateItem(Item item);
    Item reviewItem(Long itemId);
    Item releaseItem(Long itemId);
    Item rejectItem(Long itemId);
    Long removeItem(Long itemId);
    Long deleteItem(Long itemId);
}
