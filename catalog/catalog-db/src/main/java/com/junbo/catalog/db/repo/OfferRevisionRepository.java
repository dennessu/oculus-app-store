/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.OfferRevisionDao;
import com.junbo.catalog.db.entity.OfferRevisionEntity;
import com.junbo.catalog.db.mapper.OfferRevisionMapper;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer revision repository.
 */
public class OfferRevisionRepository implements BaseRevisionRepository<OfferRevision> {
    @Autowired
    private OfferRevisionDao offerRevisionDao;

    public Long create(OfferRevision offerRevision) {
        return offerRevisionDao.create(OfferRevisionMapper.toDBEntity(offerRevision));
    }

    public OfferRevision get(Long revisionId) {
        return OfferRevisionMapper.toModel(offerRevisionDao.get(revisionId));
    }

    @Override
    public Long update(OfferRevision revision) {
        OfferRevisionEntity dbEntity = offerRevisionDao.get(revision.getRevisionId());
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer-revision", revision.getRevisionId()).exception();
        }
        OfferRevisionMapper.fillDBEntity(revision, dbEntity);
        return offerRevisionDao.update(dbEntity);
    }

    @Override
    public void delete(Long revisionId) {
        OfferRevisionEntity dbEntity = offerRevisionDao.get(revisionId);
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer-revision", revisionId).exception();
        }
        dbEntity.setDeleted(true);
        offerRevisionDao.update(dbEntity);
    }
}
