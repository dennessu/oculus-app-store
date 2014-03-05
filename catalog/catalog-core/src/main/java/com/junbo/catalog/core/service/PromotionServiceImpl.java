/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.db.repo.PromotionDraftRepository;
import com.junbo.catalog.db.repo.PromotionRepository;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.promotion.Promotion;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Promotion service implementation.
 */
public class PromotionServiceImpl implements PromotionService{
    @Autowired
    private PromotionDraftRepository promotionDraftRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public Promotion createPromotion(Promotion promotion) {
        promotion.setRevision(Constants.INITIAL_CREATION_REVISION);
        promotion.setStatus(Status.DRAFT);
        Long promotionId = promotionDraftRepository.createPromotion(promotion);
        promotion.setId(promotionId);
        promotionRepository.create(promotion);
        return promotionDraftRepository.get(promotionId);
    }

    /*@Override
    public Promotion updatePromotion(Promotion updatedPromotion) {
        promotionDraftRepository.updatePromotion(updatedPromotion);
        return promotionDraftRepository.get(updatedPromotion.getId());
    }*/

    @Override
    public Promotion getPromotion(Long promotionId, EntityGetOptions options) {
        return promotionDraftRepository.get(promotionId);
    }

    @Override
    public List<Promotion> getEffectivePromotions(int page, int size) {
        return promotionDraftRepository.getEffectivePromotions(page, size);
    }
}
