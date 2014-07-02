/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.PromotionDao;
import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.db.mapper.PromotionMapper;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Promotion repository.
 */
public class PromotionRepository implements BaseEntityRepository<Promotion> {
    @Autowired(required = false)
    private PromotionDao promotionDao;

    @Override
    public Promotion create(Promotion promotion) {
        return get(promotionDao.create(PromotionMapper.toDBEntity(promotion)));
    }

    @Override
    public Promotion get(String promotionId) {
        return PromotionMapper.toModel(promotionDao.get(promotionId));
    }

    public List<Promotion> getEffectivePromotions(PromotionsGetOptions options) {
        List<PromotionEntity> promotionEntities = promotionDao.getPromotions(options);
        List<Promotion> promotions = new ArrayList<>();
        for (PromotionEntity offerEntity : promotionEntities) {
            promotions.add(PromotionMapper.toModel(offerEntity));
        }

        return promotions;
    }

    @Override
    public Promotion update(Promotion promotion) {
        PromotionEntity dbEntity = promotionDao.get(promotion.getPromotionId());
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer", promotion.getPromotionId()).exception();
        }
        PromotionMapper.fillDBEntity(promotion, dbEntity);
        return get(promotionDao.update(dbEntity));
    }

    @Override
    public void delete(String promotionId) {
        PromotionEntity dbEntity = promotionDao.get(promotionId);
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("promotion", promotionId).exception();
        }
        dbEntity.setDeleted(true);
        promotionDao.update(dbEntity);
    }
}
