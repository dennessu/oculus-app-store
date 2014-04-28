/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.common.util.EntityType;
import com.junbo.catalog.db.dao.ItemOfferRelationsDao;
import com.junbo.catalog.db.entity.ItemOfferRelationsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Item repository.
 */
public class ItemOfferRelationsRepository {
    @Autowired
    private ItemOfferRelationsDao relationsDao;

    public Long create(ItemOfferRelationsEntity relationsEntity) {
        return relationsDao.create(relationsEntity);
    }

    public ItemOfferRelationsEntity get(Long id) {
        return relationsDao.get(id);
    }

    public List<ItemOfferRelationsEntity> getRelations(Long entityId, EntityType entityType) {
        List<ItemOfferRelationsEntity> relations = relationsDao.getRelations(entityId, entityType);
        return relations;
    }

    public void delete(EntityType entityType, Long entityId, Long parentOfferId) {
        List<ItemOfferRelationsEntity> relations = relationsDao.getRelations(entityId, entityType, parentOfferId);
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }
        for (ItemOfferRelationsEntity relationsEntity : relations) {
            relationsEntity.setDeleted(true);
            relationsDao.update(relationsEntity);
        }
    }

    public void delete(Long relationId) {
        ItemOfferRelationsEntity dbEntity = relationsDao.get(relationId);
        dbEntity.setDeleted(true);
        relationsDao.update(dbEntity);
    }
}
