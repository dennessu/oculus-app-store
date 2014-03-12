/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.db.dao.ItemDao;
import com.junbo.catalog.db.entity.ItemEntity;

/**
 * Item DAO implementation.
 */
public class ItemDaoImpl extends VersionedDaoImpl<ItemEntity> implements ItemDao {
    @Override
    public ItemEntity getItem(Long itemId, Long timestamp) {
        return get(itemId, timestamp, "itemId");
    }
}
