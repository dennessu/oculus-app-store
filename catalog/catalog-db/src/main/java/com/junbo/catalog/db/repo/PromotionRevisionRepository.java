/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;
import com.junbo.catalog.db.dao.PromotionRevisionDao;
import com.junbo.catalog.db.entity.PromotionRevisionEntity;
import com.junbo.catalog.db.mapper.PromotionRevisionMapper;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Offer revision repository.
 */
public class PromotionRevisionRepository implements BaseRevisionRepository<PromotionRevision> {
    @Autowired
    private PromotionRevisionDao promotionRevisionDao;

    public Long create(PromotionRevision offerRevision) {
        return promotionRevisionDao.create(PromotionRevisionMapper.toDBEntity(offerRevision));
    }

    public PromotionRevision get(Long revisionId) {
        return PromotionRevisionMapper.toModel(promotionRevisionDao.get(revisionId));
    }

    @Override
    public Long update(PromotionRevision revision) {
        PromotionRevisionEntity dbEntity = promotionRevisionDao.get(revision.getRevisionId());
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer-revision", revision.getRevisionId()).exception();
        }
        PromotionRevisionMapper.fillDBEntity(revision, dbEntity);
        return promotionRevisionDao.update(dbEntity);
    }

    @Override
    public void delete(Long revisionId) {
        PromotionRevisionEntity dbEntity = promotionRevisionDao.get(revisionId);
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer-revision", revisionId).exception();
        }
        dbEntity.setDeleted(true);
        promotionRevisionDao.update(dbEntity);
    }
}
