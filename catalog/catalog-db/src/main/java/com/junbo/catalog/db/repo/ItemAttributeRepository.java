/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.ItemAttributeDao;
import com.junbo.catalog.db.entity.ItemAttributeEntity;
import com.junbo.catalog.db.mapper.ItemAttributeMapper;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Item repository.
 */
public class ItemAttributeRepository {
    @Autowired
    private ItemAttributeDao attributeDao;

    public Long create(ItemAttribute attribute) {
        return attributeDao.create(ItemAttributeMapper.toDBEntity(attribute));
    }

    public ItemAttribute get(Long attributeId) {
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

    public Long update(ItemAttribute attribute) {
        ItemAttributeEntity dbEntity = attributeDao.get(attribute.getId());
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("item-attribute", Utils.encodeId(attribute.getId())).exception();
        }
        ItemAttributeMapper.fillDBEntity(attribute, dbEntity);
        return attributeDao.update(dbEntity);
    }

    public void delete(Long attributeId) {
        ItemAttributeEntity dbEntity = attributeDao.get(attributeId);
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("item-attribute", Utils.encodeId(attributeId)).exception();
        }
        dbEntity.setDeleted(true);
        attributeDao.update(dbEntity);
    }
}
