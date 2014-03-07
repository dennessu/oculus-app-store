/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.ItemConverter;
import com.junbo.catalog.db.dao.ItemDao;
import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Item repository.
 */
public class ItemRepository implements EntityRepository<Item> {
    @Autowired
    private ItemDao itemDao;

    @Override
    public Long create(Item item) {
        ItemEntity entity = ItemConverter.toEntity(item);
        return itemDao.create(entity);
    }

    @Override
    public Item get(Long id, Long timestamp) {
        ItemEntity entity = itemDao.getItem(id, timestamp);
        return ItemConverter.toModel(entity);
    }
}
