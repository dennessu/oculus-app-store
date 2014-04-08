/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.ItemDao;
import com.junbo.catalog.db.mapper.ItemMapper;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Item repository.
 */
public class ItemRepository implements BaseEntityRepository<Item> {
    @Autowired
    private ItemDao itemDao;

    @Override
    public Long create(Item item) {
        return itemDao.create(ItemMapper.toDBEntity(item));
    }

    @Override
    public Item get(Long entityId) {
        return ItemMapper.toModel(itemDao.get(entityId));
    }

    @Override
    public Long update(Item entity) {
        return null;
    }
}
