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
public class PromotionDraftRepository {
    @Autowired
    private PromotionDraftDao promotionDraftDao;

    public Long createPromotion(Promotion promotion) {
        PromotionDraftEntity promotionDraftEntity = PromotionConverter.toDraftEntity(promotion);
        return promotionDraftDao.create(promotionDraftEntity);
    }

    /*public Long updatePromotion(Promotion promotion) {
        PromotionDraftEntity promotionDraftEntity = promotionDraftDao.get(promotion.getId());
        promotionDraftEntity.setName(promotion.getName());
        promotionDraftEntity.setType(promotion.getType());
        promotionDraftEntity.setStartDate(promotion.getStartDate());
        promotionDraftEntity.setEndDate(promotion.getEndDate());
        promotionDraftEntity.setPayload(Utils.toJson(promotionDraftEntity));

        return promotionDraftDao.update(promotionDraftEntity);
    }*/

    public Promotion get(Long promotionId) {
        PromotionDraftEntity promotionDraftEntity = promotionDraftDao.get(promotionId);
        return PromotionConverter.toModel(promotionDraftEntity);
    }

    public List<Promotion> getEffectivePromotions(int start, int size) {
        List<PromotionDraftEntity> entities = promotionDraftDao.getEffectivePromotions(start, size);

        List<Promotion> promotions = new ArrayList<>();
        for (PromotionDraftEntity entity : entities) {
            Promotion promotion = PromotionConverter.toModel(entity);
            promotions.add(promotion);
        }

        return promotions;
    }
}
