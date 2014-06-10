/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.ItemEntity;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.id.OrganizationId;

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
        entity.setType(model.getType());
        entity.setGenres(model.getGenres());
        entity.setOwnerId(model.getOwnerId() == null ? null : model.getOwnerId().getValue());
        entity.setCurrentRevisionId(model.getCurrentRevisionId());
        entity.setPayload(Utils.toJson(model));
        entity.setCreatedBy(String.valueOf(model.getOwnerId()));
        entity.setUpdatedBy(String.valueOf(model.getOwnerId()));
        entity.setRev(model.getResourceAge());
    }

    public static Item toModel(ItemEntity entity) {
        if (entity == null) {
            return null;
        }
        Item model = Utils.fromJson(entity.getPayload(), Item.class);
        model.setItemId(entity.getItemId());
        model.setType(entity.getType());
        model.setCurrentRevisionId(entity.getCurrentRevisionId());
        model.setOwnerId(new OrganizationId(entity.getOwnerId()));
        model.setGenres(entity.getGenres());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedTime(entity.getUpdatedTime());
        model.setResourceAge(entity.getRev());
        return model;
    }
}
