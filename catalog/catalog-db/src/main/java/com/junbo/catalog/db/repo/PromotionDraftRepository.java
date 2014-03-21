/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.PromotionConverter;
import com.junbo.catalog.db.dao.PromotionDraftDao;
import com.junbo.catalog.db.entity.PromotionDraftEntity;
import com.junbo.catalog.spec.model.promotion.Promotion;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Promotion draft repository.
 */
public class PromotionDraftRepository implements EntityDraftRepository<Promotion> {
    @Autowired
    private PromotionDraftDao promotionDraftDao;

    @Override
    public Long create(Promotion promotion) {
        PromotionDraftEntity promotionDraftEntity = PromotionConverter.toDraftEntity(promotion);
        return promotionDraftDao.create(promotionDraftEntity);
    }

    @Override
    public Long update(Promotion promotion) {
        if (promotion == null) {
            return null;
        }

        PromotionDraftEntity promotionDraftEntity = promotionDraftDao.get(promotion.getId());
        PromotionConverter.fillDraftEntity(promotion, promotionDraftEntity);

        return promotionDraftDao.update(promotionDraftEntity);
    }

    @Override
    public Promotion get(Long promotionId) {
        PromotionDraftEntity promotionDraftEntity = promotionDraftDao.get(promotionId);
        return PromotionConverter.toModel(promotionDraftEntity);
    }

    @Override
    public List<Promotion> getEntities(int start, int size, String status) {
        return getEffectivePromotions(start, size, status);
    }

    public List<Promotion> getEffectivePromotions(int start, int size, String status) {
        List<PromotionDraftEntity> entities = promotionDraftDao.getEffectivePromotions(start, size, status);

        List<Promotion> promotions = new ArrayList<>();
        for (PromotionDraftEntity entity : entities) {
            Promotion promotion = PromotionConverter.toModel(entity);
            promotions.add(promotion);
        }

        return promotions;
    }
}
