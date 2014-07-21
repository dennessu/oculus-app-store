/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.sql;

import com.junbo.catalog.db.dao.OfferAttributeDao;
import com.junbo.catalog.db.entity.OfferAttributeEntity;
import com.junbo.catalog.db.mapper.OfferAttributeMapper;
import com.junbo.catalog.db.repo.OfferAttributeRepository;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer repository.
 */
public class OfferAttributeRepositoryImpl implements OfferAttributeRepository {
    @Autowired
    private OfferAttributeDao attributeDao;

    public OfferAttribute create(OfferAttribute attribute) {
        return get(attributeDao.create(OfferAttributeMapper.toDBEntity(attribute)));
    }

    public OfferAttribute get(String attributeId) {
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

    public OfferAttribute update(OfferAttribute attribute, OfferAttribute oldAttribute) {
        OfferAttributeEntity dbEntity = attributeDao.get(attribute.getId());
        OfferAttributeMapper.fillDBEntity(attribute, dbEntity);
        return get(attributeDao.update(dbEntity));
    }


    public void delete(String attributeId) {
        OfferAttributeEntity dbEntity = attributeDao.get(attributeId);
        dbEntity.setDeleted(true);
        attributeDao.update(dbEntity);
    }
}
