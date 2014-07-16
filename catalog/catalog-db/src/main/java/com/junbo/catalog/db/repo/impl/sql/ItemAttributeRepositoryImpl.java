/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.sql;

import com.junbo.catalog.db.dao.ItemAttributeDao;
import com.junbo.catalog.db.entity.ItemAttributeEntity;
import com.junbo.catalog.db.mapper.ItemAttributeMapper;
import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Item repository.
 */
public class ItemAttributeRepositoryImpl implements ItemAttributeRepository {
    @Autowired
    private ItemAttributeDao attributeDao;

    public ItemAttribute create(ItemAttribute attribute) {
        return get(attributeDao.create(ItemAttributeMapper.toDBEntity(attribute)));
    }

    public ItemAttribute get(String attributeId) {
        ItemAttributeEntity dbEntity = attributeDao.get(attributeId);
        return ItemAttributeMapper.toModel(dbEntity);
    }

    public List<ItemAttribute> getAttributes(ItemAttributesGetOptions options) {
        List<ItemAttributeEntity> attributeEntities = attributeDao.getAttributes(options);
        List<ItemAttribute> attributes = new ArrayList<>();
        for (ItemAttributeEntity attributeEntity : attributeEntities) {
            attributes.add(ItemAttributeMapper.toModel(attributeEntity));
        }

        return attributes;
    }

    public ItemAttribute update(ItemAttribute attribute, ItemAttribute oldAttribute) {
        ItemAttributeEntity dbEntity = attributeDao.get(attribute.getId());
        ItemAttributeMapper.fillDBEntity(attribute, dbEntity);
        return get(attributeDao.update(dbEntity));
    }

    public void delete(String attributeId) {
        ItemAttributeEntity dbEntity = attributeDao.get(attributeId);
        dbEntity.setDeleted(true);
        attributeDao.update(dbEntity);
    }
}
