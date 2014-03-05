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
public class ItemRepository {
    @Autowired
    private ItemDao itemDao;

    public Long create(Item item) {
        ItemEntity entity = ItemConverter.toEntity(item);
        return itemDao.create(entity);
    }

    public Item get(Long id, Integer revision) {
        ItemEntity entity = itemDao.getItem(id, revision);
        return ItemConverter.toModel(entity);
    }
}
