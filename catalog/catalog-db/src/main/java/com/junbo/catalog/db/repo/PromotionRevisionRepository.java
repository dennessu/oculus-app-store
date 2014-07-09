/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;
import com.junbo.catalog.db.dao.PromotionRevisionDao;
import com.junbo.catalog.db.entity.PromotionRevisionEntity;
import com.junbo.catalog.db.mapper.PromotionRevisionMapper;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import com.junbo.common.error.AppCommonErrors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer revision repository.
 */
public class PromotionRevisionRepository implements BaseRevisionRepository<PromotionRevision> {
    @Autowired(required = false)
    private PromotionRevisionDao promotionRevisionDao;

    public PromotionRevision create(PromotionRevision promotionRevision) {
        return get(promotionRevisionDao.create(PromotionRevisionMapper.toDBEntity(promotionRevision)));
    }

    public PromotionRevision get(String revisionId) {
        return PromotionRevisionMapper.toModel(promotionRevisionDao.get(revisionId));
    }

    public List<PromotionRevision> getRevisions(PromotionRevisionsGetOptions options) {
        List<PromotionRevisionEntity> revisionEntities = promotionRevisionDao.getEffectiveRevisions(options);
        List<PromotionRevision> revisions = new ArrayList<>();
        for (PromotionRevisionEntity revisionEntity : revisionEntities) {
            revisions.add(PromotionRevisionMapper.toModel(revisionEntity));
        }

        return revisions;
    }

    @Override
    public PromotionRevision update(PromotionRevision revision) {
        PromotionRevisionEntity dbEntity = promotionRevisionDao.get(revision.getRevisionId());
        if (dbEntity == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("promotion-revision", revision.getRevisionId()).exception();
        }
        PromotionRevisionMapper.fillDBEntity(revision, dbEntity);
        return get(promotionRevisionDao.update(dbEntity));
    }

    @Override
    public void delete(String revisionId) {
        PromotionRevisionEntity dbEntity = promotionRevisionDao.get(revisionId);
        if (dbEntity == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("promotion-revision", revisionId).exception();
        }
        dbEntity.setDeleted(true);
        promotionRevisionDao.update(dbEntity);
    }
}
