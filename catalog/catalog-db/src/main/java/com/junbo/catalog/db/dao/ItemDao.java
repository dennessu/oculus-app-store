/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;

import java.util.Collection;
import java.util.List;

/**
 * Item DAO definition.
 */
public interface ItemDao extends BaseDao<ItemEntity> {
    List<ItemEntity> getItems(ItemsGetOptions options);
    List<ItemEntity> getItems(Collection<Long> itemIds);
}
