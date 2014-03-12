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
}
