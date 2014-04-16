/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.AttributeDao;
import com.junbo.catalog.db.entity.AttributeEntity;
import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.common.LocalizableProperty;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Item repository.
 */
public class AttributeRepository {
    @Autowired
    private AttributeDao attributeDao;

    public Long create(Attribute attribute) {
        AttributeEntity entity = new AttributeEntity();
        entity.setName(Utils.toJson(attribute.getName()));
        entity.setType(attribute.getType());
        entity.setPayload(Utils.toJson(attribute));
        return attributeDao.create(entity);
    }

    public Attribute get(Long id) {
        AttributeEntity entity = attributeDao.get(id);
        return toModel(entity);
    }

    public List<Attribute> getAttributes(int start, int size, String attributeType) {
        List<AttributeEntity> attributeEntities = attributeDao.getAttributes(start, size, attributeType);
        List<Attribute> attributes = new ArrayList<>();
        for (AttributeEntity attributeEntity : attributeEntities) {
            attributes.add(toModel(attributeEntity));
        }

        return attributes;
    }

    private Attribute toModel(AttributeEntity dbEntity) {
        // TODO: extract these to a mapper
        Attribute attribute = Utils.fromJson(dbEntity.getPayload(), Attribute.class);
        attribute.setId(dbEntity.getId());
        attribute.setName(Utils.fromJson(dbEntity.getName(), LocalizableProperty.class));
        attribute.setType(dbEntity.getType());
        attribute.setCreatedBy(dbEntity.getCreatedBy());
        attribute.setCreatedTime(dbEntity.getCreatedTime());
        attribute.setUpdatedBy(dbEntity.getUpdatedBy());
        attribute.setUpdatedTime(dbEntity.getUpdatedTime());

        return attribute;
    }
}
