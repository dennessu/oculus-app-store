/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.db.entity.PromotionRevisionEntity;
import com.junbo.catalog.db.repo.PromotionRepository;
import com.junbo.catalog.db.repo.PromotionRevisionRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Promotion service implementation.
 */
public class PromotionServiceImpl extends BaseRevisionedServiceImpl<Promotion, PromotionRevision>
        implements PromotionService {
    @Autowired
    private PromotionRepository promotionRepo;
    @Autowired
    private PromotionRevisionRepository promotionRevisionRepo;

    @Override
    public Promotion createEntity(Promotion promotion) {
        if (Boolean.TRUE.equals(promotion.getCurated())) {
            throw AppErrors.INSTANCE
                    .fieldNotCorrect("curated", "Cannot create an promotion with curated true.").exception();
        }
        validatePromotion(promotion);
        return super.createEntity(promotion);
    }

    @Override
    public Promotion updateEntity(Long promotionId, Promotion promotion) {
        validateId(promotionId, promotion.getPromotionId());
        validatePromotion(promotion);
        return super.updateEntity(promotionId, promotion);
    }

    @Override
    public PromotionRevision createRevision(PromotionRevision revision) {
        validateRevision(revision);
        return super.createRevision(revision);
    }

    @Override
    public PromotionRevision updateRevision(Long revisionId, PromotionRevision revision) {
        validateId(revisionId, revision.getRevisionId());
        validateRevision(revision);

        PromotionRevision existingRevision = promotionRevisionRepo.get(revisionId);
        if (Status.APPROVED.equals(existingRevision.getStatus())) {
            throw AppErrors.INSTANCE.validation("Cannot update a revision after it's approved.").exception();
        }
        checkEntityNotNull(revisionId, existingRevision, "promotion-revision");

        if (Status.APPROVED.equals(revision.getStatus())) {
            Promotion existingPromotion = promotionRepo.get(revision.getEntityId());
            checkEntityNotNull(revision.getEntityId(), existingPromotion, getEntityType());
            existingPromotion.setCurated(Boolean.TRUE);
            existingPromotion.setCurrentRevisionId(revisionId);
            existingPromotion.setStartDate(revision.getStartDate());
            existingPromotion.setEndDate(revision.getEndDate());
            getEntityRepo().update(existingPromotion);
            revision.setTimestamp(Utils.now());
        }
        promotionRevisionRepo.update(revision);
        return promotionRevisionRepo.get(revisionId);
    }

    @Override
    public List<Promotion> getEffectivePromotions(PromotionsGetOptions options) {
        return promotionRepo.getEffectivePromotions(options);
    }

    @Override
    public List<PromotionRevision> getRevisions(PromotionRevisionsGetOptions options) {
        return promotionRevisionRepo.getRevisions(options);
    }

    @Override
    protected PromotionRepository getEntityRepo() {
        return promotionRepo;
    }

    @Override
    protected PromotionRevisionRepository getRevisionRepo() {
        return promotionRevisionRepo;
    }

    @Override
    protected String getRevisionType() {
        return "promotion-revision";
    }

    @Override
    protected String getEntityType() {
        return "promotion";
    }

    private void validatePromotion(Promotion promotion) {
        checkFieldNotNull(promotion.getName(), "name");
        checkFieldNotNull(promotion.getOwnerId(), "publisher");
        checkFieldNotNull(promotion.getType(), "type");
    }

    private void validateRevision(PromotionRevision revision) {
        checkFieldNotNull(revision.getOwnerId(), "publisher");
        checkFieldNotNull(revision.getPromotionId(), "promotion");
        checkFieldNotNull(revision.getStartDate(), "startDate");
        checkFieldNotNull(revision.getEndDate(), "endDate");
    }
}
