/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.ItemDao;
import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.db.mapper.ItemMapper;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

    public List<Item> getItems(ItemsGetOptions options) {
        List<ItemEntity> itemEntities = itemDao.getItems(options);
        List<Item> items = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntities) {
            items.add(ItemMapper.toModel(itemEntity));
        }

        return items;
    }

    @Override
    public Long update(Item item) {
        ItemEntity dbEntity = itemDao.get(item.getItemId());
        ItemMapper.fillDBEntity(item, dbEntity);
        return itemDao.update(dbEntity);
    }

    @Override
    public void delete(Long itemId) {
        ItemEntity dbEntity = itemDao.get(itemId);
        dbEntity.setDeleted(true);
        itemDao.update(dbEntity);
    }
}
