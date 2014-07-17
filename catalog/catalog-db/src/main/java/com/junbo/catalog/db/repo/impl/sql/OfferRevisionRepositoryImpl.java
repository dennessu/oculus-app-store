/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.sql;

import com.junbo.catalog.db.dao.OfferRevisionDao;
import com.junbo.catalog.db.entity.OfferRevisionEntity;
import com.junbo.catalog.db.mapper.OfferRevisionMapper;
import com.junbo.catalog.db.repo.OfferRevisionRepository;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Offer revision repository.
 */
public class OfferRevisionRepositoryImpl implements OfferRevisionRepository {
    @Autowired
    private OfferRevisionDao offerRevisionDao;

    @Override
    public OfferRevision create(OfferRevision offerRevision) {
        return get(offerRevisionDao.create(OfferRevisionMapper.toDBEntity(offerRevision)));
    }

    @Override
    public OfferRevision get(String revisionId) {
        return OfferRevisionMapper.toModel(offerRevisionDao.get(revisionId));
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevisionEntity> revisionEntities = offerRevisionDao.getRevisions(options);
        List<OfferRevision> revisions = new ArrayList<>();
        for (OfferRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(OfferRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    @Override
    public List<OfferRevision> getRevisions(Collection<String> offerIds, Long timestamp) {
        List<OfferRevisionEntity> revisionEntities = new ArrayList<>();
        for (String offerId : offerIds) {
            OfferRevisionEntity revisionEntity = offerRevisionDao.getRevision(offerId, timestamp);
            if (revisionEntity != null) {
                revisionEntities.add(revisionEntity);
            }
        }
        List<OfferRevision> revisions = new ArrayList<>();
        for (OfferRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(OfferRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    @Override
    public List<OfferRevision> getRevisions(String itemId) {
        List<OfferRevisionEntity> revisionEntities = offerRevisionDao.getRevisionsByItemId(itemId);
        List<OfferRevision> revisions = new ArrayList<>();
        for (OfferRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(OfferRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    @Override
    public List<OfferRevision> getRevisionsBySubOfferId(String offerId) {
        List<OfferRevisionEntity> revisionEntities = offerRevisionDao.getRevisionsBySubOfferId(offerId);
        List<OfferRevision> revisions = new ArrayList<>();
        for (OfferRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(OfferRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    @Override
    public OfferRevision update(OfferRevision revision, OfferRevision oldRevision) {
        OfferRevisionEntity dbEntity = offerRevisionDao.get(revision.getRevisionId());
        OfferRevisionMapper.fillDBEntity(revision, dbEntity);
        return get(offerRevisionDao.update(dbEntity));
    }

    @Override
    public void delete(String revisionId) {
        OfferRevisionEntity dbEntity = offerRevisionDao.get(revisionId);
        dbEntity.setDeleted(true);
        offerRevisionDao.update(dbEntity);
    }
}
