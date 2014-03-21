/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.AttributeDao;
import com.junbo.catalog.db.entity.AttributeEntity;
import com.junbo.catalog.spec.model.attribute.Attribute;
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
        entity.setName(attribute.getName());
        entity.setType(attribute.getType());
        return attributeDao.create(entity);
    }

    public Attribute get(Long id) {
        AttributeEntity entity = attributeDao.get(id);
        Attribute attribute = new Attribute();
        attribute.setId(entity.getId());
        attribute.setName(entity.getName());
        attribute.setType(entity.getType());
        return attribute;
    }

    public List<Attribute> getAttributes(int start, int size, String attributeType) {
        List<AttributeEntity> attributeEntities = attributeDao.getAttributes(start, size, attributeType);
        List<Attribute> attributes = new ArrayList<>();
        for (AttributeEntity attributeEntity : attributeEntities) {
            Attribute attribute = new Attribute();
            // TODO: extract these to a mapper
            attribute.setId(attributeEntity.getId());
            attribute.setName(attributeEntity.getName());
            attribute.setType(attributeEntity.getType());
            attribute.setCreatedBy(attributeEntity.getCreatedBy());
            attribute.setCreatedTime(attributeEntity.getCreatedTime());
            attribute.setUpdatedBy(attributeEntity.getUpdatedBy());
            attribute.setUpdatedTime(attributeEntity.getUpdatedTime());

            attributes.add(attribute);
        }

        return attributes;
    }
}
