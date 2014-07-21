/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.sql;

import com.junbo.catalog.db.dao.ItemDao;
import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.db.mapper.ItemMapper;
import com.junbo.catalog.db.repo.ItemRepository;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Item repository.
 */
public class ItemRepositoryImpl implements ItemRepository {
    @Autowired
    private ItemDao itemDao;

    @Override
    public Item create(Item item) {
        return get(itemDao.create(ItemMapper.toDBEntity(item)));
    }

    @Override
    public Item get(String entityId) {
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
    public List<Item> getItems(Collection<String> itemIds) {
        List<ItemEntity> itemEntities = itemDao.getItems(itemIds);
        List<Item> items = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntities) {
            items.add(ItemMapper.toModel(itemEntity));
        }

        return items;
    }

    @Override
    public Item update(Item item, Item oldItem) {
        ItemEntity dbEntity = itemDao.get(item.getItemId());
        ItemMapper.fillDBEntity(item, dbEntity);
        return get(itemDao.update(dbEntity));
    }

    @Override
    public void delete(String itemId) {
        ItemEntity dbEntity = itemDao.get(itemId);
        dbEntity.setDeleted(true);
        itemDao.update(dbEntity);
    }
}
