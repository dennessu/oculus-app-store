/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.ItemAttributeEntity;
import com.junbo.catalog.spec.model.item.ItemAttribute;

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
    }

    public static ItemAttribute toModel(ItemAttributeEntity dbEntity) {
        if (dbEntity == null) {
            return null;
        }
        ItemAttribute attribute = Utils.fromJson(dbEntity.getPayload(), ItemAttribute.class);
        attribute.setId(dbEntity.getId());
        attribute.setParentId(dbEntity.getParentId());
        attribute.setType(dbEntity.getType());
        attribute.setCreatedBy(dbEntity.getCreatedBy());
        attribute.setCreatedTime(dbEntity.getCreatedTime());
        attribute.setUpdatedBy(dbEntity.getUpdatedBy());
        attribute.setUpdatedTime(dbEntity.getUpdatedTime());
        return attribute;
    }
}
