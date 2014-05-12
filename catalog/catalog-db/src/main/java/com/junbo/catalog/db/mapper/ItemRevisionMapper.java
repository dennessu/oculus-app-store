/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.ItemRevisionEntity;
import com.junbo.catalog.spec.model.item.ItemRevision;

/**
 * Item revision mapper between model and DB entity.
 */
public class ItemRevisionMapper {
    private ItemRevisionMapper(){}

    public static ItemRevisionEntity toDBEntity(ItemRevision model) {
        if (model == null) {
            return null;
        }
        ItemRevisionEntity entity = new ItemRevisionEntity();
        fillDBEntity(model, entity);
        return entity;
    }

    public static void fillDBEntity(ItemRevision model, ItemRevisionEntity entity) {
        entity.setRevisionId(model.getRevisionId());
        entity.setItemId(model.getItemId());
        entity.setStatus(model.getStatus());
        entity.setOwnerId(model.getOwnerId());
        entity.setTimestamp(model.getTimestamp());
        entity.setPayload(Utils.toJson(model));
        entity.setCreatedBy(String.valueOf(model.getOwnerId()));
        entity.setUpdatedBy(String.valueOf(model.getOwnerId()));
        entity.setRev(model.getRev()==null ? null : Integer.valueOf(model.getRev()));
    }

    public static ItemRevision toModel(ItemRevisionEntity entity) {
        if (entity == null) {
            return null;
        }
        ItemRevision model = Utils.fromJson(entity.getPayload(), ItemRevision.class);
        model.setItemId(entity.getItemId());
        model.setStatus(entity.getStatus());
        model.setOwnerId(entity.getOwnerId());
        model.setRevisionId(entity.getRevisionId());
        model.setTimestamp(entity.getTimestamp());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedTime(entity.getUpdatedTime());
        model.setRev(entity.getRev().toString());
        return model;
    }
}
