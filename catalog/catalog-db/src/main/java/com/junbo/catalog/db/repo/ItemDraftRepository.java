/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.ItemConverter;
import com.junbo.catalog.db.dao.ItemDraftDao;
import com.junbo.catalog.db.entity.ItemDraftEntity;
import com.junbo.catalog.spec.model.item.Item;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Item draft repository.
 */
public class ItemDraftRepository implements EntityDraftRepository<Item> {
    @Autowired
    private ItemDraftDao itemDraftDao;

    @Override
    public Long create(Item item) {
        ItemDraftEntity entity = ItemConverter.toDraftEntity(item);
        return itemDraftDao.create(entity);
    }

    @Override
    public Item get(Long itemId) {
        ItemDraftEntity entity = itemDraftDao.get(itemId);
        return ItemConverter.toModel(entity);
    }

    @Override
    public List<Item> getEntities(int start, int size) {
        return getItems(start, size);
    }

    public List<Item> getItems(int start, int size) {
        List<ItemDraftEntity> entities = itemDraftDao.getItems(start, size);
        List<Item> result = new ArrayList<>();
        for (ItemDraftEntity entity : entities) {
            result.add(ItemConverter.toModel(entity));
        }

        return result;
    }

    public Long update(Item item) {
        if (item == null) {
            return null;
        }

        ItemDraftEntity entity = itemDraftDao.get(item.getId());
        ItemConverter.fillDraftEntity(item, entity);

        return itemDraftDao.update(entity);
    }
}
