/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.convertor;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.ItemDraftEntity;
import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.spec.model.item.Item;

/**
 * Item converter between model and DB entity.
 */
public class ItemConverter {
    private ItemConverter(){}

    public static ItemDraftEntity toDraftEntity(Item model) {
        ItemDraftEntity entity = new ItemDraftEntity();
        entity.setType(model.getType());
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setRevision(model.getRevision());
        entity.setOwnerId(model.getOwnerId());
        entity.setPayload(Utils.toJson(model));
        return entity;
    }

    public static Item toModel(ItemDraftEntity entity) {
        Item model = Utils.fromJson(entity.getPayload(), Item.class);
        model.setId(entity.getId());
        model.setType(entity.getType());
        model.setName(entity.getName());
        model.setRevision(entity.getRevision());
        model.setOwnerId(entity.getOwnerId());
        model.setStatus(entity.getStatus());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }

    public static ItemEntity toEntity(Item model) {
        ItemEntity entity = new ItemEntity();
        entity.setItemId(model.getId());
        entity.setType(model.getType());
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setRevision(model.getRevision());
        entity.setOwnerId(model.getOwnerId());
        entity.setPayload(Utils.toJson(model));
        return entity;
    }


    public static Item toModel(ItemEntity entity) {
        Item model = Utils.fromJson(entity.getPayload(), Item.class);
        model.setId(entity.getItemId());
        model.setType(entity.getType());
        model.setName(entity.getName());
        model.setStatus(entity.getStatus());
        model.setOwnerId(entity.getOwnerId());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }
}

