/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;
import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.spec.model.item.Item;

/**
 * Item mapper between model and DB entity.
 */
public class ItemMapper {
    private ItemMapper(){}

    public static ItemEntity toDBEntity(Item model) {
        if (model == null) {
            return null;
        }
        ItemEntity entity = new ItemEntity();
        fillDBEntity(model, entity);
        return entity;
    }

    public static void fillDBEntity(Item model, ItemEntity entity) {
        entity.setItemId(model.getItemId());
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setOwnerId(model.getOwnerId());
        entity.setCurrentRevisionId(model.getCurrentRevisionId());
    }

    public static Item toModel(ItemEntity entity) {
        if (entity == null) {
            return null;
        }
        Item model = new Item();
        model.setItemId(entity.getItemId());
        model.setName(entity.getName());
        model.setStatus(entity.getStatus());
        model.setCurrentRevisionId(entity.getCurrentRevisionId());
        model.setOwnerId(entity.getOwnerId());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }
}
