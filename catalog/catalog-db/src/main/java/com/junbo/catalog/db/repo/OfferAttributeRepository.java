/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.OfferAttributeDao;
import com.junbo.catalog.db.entity.OfferAttributeEntity;
import com.junbo.catalog.db.mapper.OfferAttributeMapper;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer repository.
 */
public class OfferAttributeRepository implements AttributeRepository<OfferAttribute> {
    @Autowired
    private OfferAttributeDao attributeDao;

    public Long create(OfferAttribute attribute) {
        return attributeDao.create(OfferAttributeMapper.toDBEntity(attribute));
    }

    public OfferAttribute get(Long attributeId) {
        OfferAttributeEntity dbEntity = attributeDao.get(attributeId);
        return OfferAttributeMapper.toModel(dbEntity);
    }

    public List<OfferAttribute> getAttributes(OfferAttributesGetOptions options) {
        List<OfferAttributeEntity> attributeEntities = attributeDao.getAttributes(options);
        List<OfferAttribute> attributes = new ArrayList<>();
        for (OfferAttributeEntity attributeEntity : attributeEntities) {
            attributes.add(OfferAttributeMapper.toModel(attributeEntity));
        }

        return attributes;
    }

    public Long update(OfferAttribute attribute) {
        OfferAttributeEntity dbEntity = attributeDao.get(attribute.getId());
        OfferAttributeMapper.fillDBEntity(attribute, dbEntity);
        return attributeDao.update(dbEntity);
    }


    public void delete(Long attributeId) {
        OfferAttributeEntity dbEntity = attributeDao.get(attributeId);
        dbEntity.setDeleted(true);
        attributeDao.update(dbEntity);
    }
}
