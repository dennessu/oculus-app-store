/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.db.repo.PromotionRepository;
import com.junbo.catalog.db.repo.PromotionRevisionRepository;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import com.junbo.common.error.AppCommonErrors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Promotion service implementation.
 */
public class PromotionServiceImpl extends BaseRevisionedServiceImpl<Promotion, PromotionRevision>
        implements PromotionService {
    @Autowired(required = false)
    private PromotionRepository promotionRepo;
    @Autowired(required = false)
    private PromotionRevisionRepository promotionRevisionRepo;

    @Override
    public Promotion createEntity(Promotion promotion) {
        if (promotion.getRev() != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull("rev").exception();
        }
        validatePromotion(promotion);
        return promotionRepo.create(promotion);
    }

    @Override
    public Promotion updateEntity(String promotionId, Promotion promotion) {
        validateId(promotionId, promotion.getPromotionId());
        validatePromotion(promotion);
        return super.updateEntity(promotionId, promotion);
    }

    @Override
    public PromotionRevision createRevision(PromotionRevision revision) {
        if (revision.getRev() != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull("rev").exception();
        }
        validateRevision(revision);
        return promotionRevisionRepo.create(revision);
    }

    @Override
    public PromotionRevision updateRevision(String revisionId, PromotionRevision revision) {
        validateId(revisionId, revision.getRevisionId());
        validateRevision(revision);

        PromotionRevision existingRevision = promotionRevisionRepo.get(revisionId);
        if (Status.APPROVED.is(existingRevision.getStatus())) {
            throw AppCommonErrors.INSTANCE.invalidOperation("Cannot update a revision after it's approved.").exception();
        }
        checkEntityNotNull(revisionId, existingRevision, "promotion-revision");

        if (Status.APPROVED.is(revision.getStatus())) {
            Promotion existingPromotion = promotionRepo.get(revision.getEntityId());
            checkEntityNotNull(revision.getEntityId(), existingPromotion, getEntityType());
            existingPromotion.setCurrentRevisionId(revisionId);
            getEntityRepo().update(existingPromotion);
            revision.setTimestamp(Utils.currentTimestamp());
        }
        return promotionRevisionRepo.update(revision);
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
        checkFieldNotNull(promotion.getOwnerId(), "publisher");
        checkFieldNotNull(promotion.getType(), "type");
    }

    private void validateRevision(PromotionRevision revision) {
        checkFieldNotNull(revision.getOwnerId(), "publisher");
        checkFieldNotNull(revision.getPromotionId(), "promotion");
        checkFieldNotNull(revision.getStartDate(), "startDate");
        checkFieldNotNull(revision.getEndDate(), "endDate");
    }

    private void checkFieldNotNull(Object field, String fieldName) {
        if (field == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception();
        }
    }

    private void validateId(String expectedId, String actualId) {
        if (actualId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("id").exception();
        }
        if (!expectedId.equals(actualId)) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable("id", actualId, expectedId).exception();
        }
    }
}
