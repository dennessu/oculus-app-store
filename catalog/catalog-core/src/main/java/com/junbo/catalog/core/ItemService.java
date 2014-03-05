/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Item service definition.
 */
@Transactional
public interface ItemService {
    Item getItem(Long id, EntityGetOptions options);
    List<Item> getItems(int start, int size);
    Item createItem(Item item);
}
