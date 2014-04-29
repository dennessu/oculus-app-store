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
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.common.id.ItemId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        List<ItemRevisionEntity> revisionEntities = itemRevisionDao.getRevisions(options);
        List<ItemRevision> revisions = new ArrayList<>();
        for (ItemRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(ItemRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    public List<ItemRevision> getRevisions(List<ItemId> itemIds, Long timestamp) {
        List<ItemRevisionEntity> revisionEntities = new ArrayList<>();
        for (ItemId itemId : itemIds) {
            ItemRevisionEntity revisionEntity = itemRevisionDao.getRevision(itemId.getValue(), timestamp);
            if (revisionEntity != null) {
                revisionEntities.add(revisionEntity);
            }
        }
        List<ItemRevision> revisions = new ArrayList<>();
        for (ItemRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(ItemRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    @Override
    public Long update(ItemRevision revision) {
        ItemRevisionEntity dbEntity = itemRevisionDao.get(revision.getRevisionId());
        ItemRevisionMapper.fillDBEntity(revision, dbEntity);
        return itemRevisionDao.update(dbEntity);
    }

    @Override
    public void delete(Long revisionId) {
        ItemRevisionEntity dbEntity = itemRevisionDao.get(revisionId);
        dbEntity.setDeleted(true);
        itemRevisionDao.update(dbEntity);
    }
}
