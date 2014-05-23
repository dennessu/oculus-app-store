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
import com.junbo.common.id.OfferId;
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

    public OfferRevision create(OfferRevision offerRevision) {
        return get(offerRevisionDao.create(OfferRevisionMapper.toDBEntity(offerRevision)));
    }

    public OfferRevision get(Long revisionId) {
        return OfferRevisionMapper.toModel(offerRevisionDao.get(revisionId));
    }

    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevisionEntity> revisionEntities = offerRevisionDao.getRevisions(options);
        List<OfferRevision> revisions = new ArrayList<>();
        for (OfferRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(OfferRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    public List<OfferRevision> getRevisions(Collection<OfferId> offerIds, Long timestamp) {
        List<OfferRevisionEntity> revisionEntities = new ArrayList<>();
        for (OfferId offerId : offerIds) {
            OfferRevisionEntity revisionEntity = offerRevisionDao.getRevision(offerId.getValue(), timestamp);
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
    public OfferRevision update(OfferRevision revision) {
        OfferRevisionEntity dbEntity = offerRevisionDao.get(revision.getRevisionId());
        OfferRevisionMapper.fillDBEntity(revision, dbEntity);
        return get(offerRevisionDao.update(dbEntity));
    }

    @Override
    public void delete(Long revisionId) {
        OfferRevisionEntity dbEntity = offerRevisionDao.get(revisionId);
        dbEntity.setDeleted(true);
        offerRevisionDao.update(dbEntity);
    }
}
