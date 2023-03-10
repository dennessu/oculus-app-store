/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.OfferAttributeEntity;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;

/**
 * Offer attribute mapper between model and DB entity.
 */
public class OfferAttributeMapper {
    private OfferAttributeMapper(){}

    public static OfferAttributeEntity toDBEntity(OfferAttribute attribute) {
        if (attribute == null) {
            return null;
        }
        OfferAttributeEntity dbEntity = new OfferAttributeEntity();
        fillDBEntity(attribute, dbEntity);
        return dbEntity;
    }

    public static void fillDBEntity(OfferAttribute attribute, OfferAttributeEntity dbEntity) {
        dbEntity.setId(attribute.getId());
        dbEntity.setType(attribute.getType());
        dbEntity.setParentId(attribute.getParentId());
        dbEntity.setPayload(Utils.toJson(attribute));
        dbEntity.setRev(attribute.getResourceAge());
    }

    public static OfferAttribute toModel(OfferAttributeEntity dbEntity) {
        if (dbEntity == null) {
            return null;
        }
        OfferAttribute attribute = Utils.fromJson(dbEntity.getPayload(), OfferAttribute.class);
        attribute.setId(dbEntity.getId());
        attribute.setParentId(dbEntity.getParentId());
        attribute.setType(dbEntity.getType());
        attribute.setCreatedTime(dbEntity.getCreatedTime());
        attribute.setUpdatedTime(dbEntity.getUpdatedTime());
        attribute.setResourceAge(dbEntity.getRev());
        return attribute;
    }
}
