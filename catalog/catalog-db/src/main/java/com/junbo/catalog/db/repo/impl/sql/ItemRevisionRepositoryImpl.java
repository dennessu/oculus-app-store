/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.sql;

import com.junbo.catalog.db.dao.ItemRevisionDao;
import com.junbo.catalog.db.entity.ItemRevisionEntity;
import com.junbo.catalog.db.mapper.ItemRevisionMapper;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.common.id.ItemId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Offer revision repository.
 */
public class ItemRevisionRepositoryImpl implements ItemRevisionRepository {
    @Autowired
    private ItemRevisionDao itemRevisionDao;

    public ItemRevision create(ItemRevision itemRevision) {
        return get(itemRevisionDao.create(ItemRevisionMapper.toDBEntity(itemRevision)));
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

    public List<ItemRevision> getRevisions(Collection<ItemId> itemIds, Long timestamp) {
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
    public List<ItemRevision> getRevisions(Long hostItemId) {
        List<ItemRevisionEntity> revisionEntities = itemRevisionDao.getRevisions(hostItemId);

        List<ItemRevision> revisions = new ArrayList<>();
        for (ItemRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(ItemRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    @Override
    public ItemRevision update(ItemRevision revision) {
        ItemRevisionEntity dbEntity = itemRevisionDao.get(revision.getRevisionId());
        ItemRevisionMapper.fillDBEntity(revision, dbEntity);
        return get(itemRevisionDao.update(dbEntity));
    }

    @Override
    public void delete(Long revisionId) {
        ItemRevisionEntity dbEntity = itemRevisionDao.get(revisionId);
        dbEntity.setDeleted(true);
        itemRevisionDao.update(dbEntity);
    }
}
