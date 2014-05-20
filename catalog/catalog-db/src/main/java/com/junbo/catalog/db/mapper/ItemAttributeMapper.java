/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.ItemAttributeEntity;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;

/**
 * Item attribute mapper between model and DB entity.
 */
public class ItemAttributeMapper {
    private ItemAttributeMapper(){}

    public static ItemAttributeEntity toDBEntity(ItemAttribute attribute) {
        if (attribute == null) {
            return null;
        }
        ItemAttributeEntity dbEntity = new ItemAttributeEntity();
        fillDBEntity(attribute, dbEntity);
        return dbEntity;
    }

    public static void fillDBEntity(ItemAttribute attribute, ItemAttributeEntity dbEntity) {
        dbEntity.setId(attribute.getId());
        dbEntity.setType(attribute.getType());
        dbEntity.setParentId(attribute.getParentId());
        dbEntity.setPayload(Utils.toJson(attribute));
        dbEntity.setRev(attribute.getResourceAge());
    }

    public static ItemAttribute toModel(ItemAttributeEntity dbEntity) {
        if (dbEntity == null) {
            return null;
        }
        ItemAttribute attribute = Utils.fromJson(dbEntity.getPayload(), ItemAttribute.class);
        attribute.setId(dbEntity.getId());
        attribute.setParentId(dbEntity.getParentId());
        attribute.setType(dbEntity.getType());
        attribute.setCreatedTime(dbEntity.getCreatedTime());
        attribute.setUpdatedTime(dbEntity.getUpdatedTime());
        attribute.setResourceAge(dbEntity.getRev());
        return attribute;
    }
}
