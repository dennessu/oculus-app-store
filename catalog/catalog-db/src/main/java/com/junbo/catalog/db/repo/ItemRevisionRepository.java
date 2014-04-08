/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.ItemRevisionDao;
import com.junbo.catalog.db.entity.ItemRevisionEntity;
import com.junbo.catalog.db.mapper.ItemRevisionMapper;
import com.junbo.catalog.spec.model.item.ItemRevision;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer revision repository.
 */
public class ItemRevisionRepository implements BaseRevisionRepository<ItemRevision> {
    @Autowired
    private ItemRevisionDao itemRevisionDao;

    public Long create(ItemRevision itemRevision) {
        return itemRevisionDao.create(ItemRevisionMapper.toDBEntity(itemRevision));
    }

    public ItemRevision get(Long revisionId) {
        return ItemRevisionMapper.toModel(itemRevisionDao.get(revisionId));
    }

    @Override
    public Long update(ItemRevision revision) {
        ItemRevisionEntity dbEntity = itemRevisionDao.get(revision.getRevisionId());
        ItemRevisionMapper.fillDBEntity(revision, dbEntity);
        return itemRevisionDao.update(dbEntity);
    }
}
