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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Promotion repository.
 */
public class PromotionRepository implements BaseEntityRepository<Promotion> {
    @Autowired
    private PromotionDao promotionDao;

    @Override
    public Long create(Promotion promotion) {
        return promotionDao.create(PromotionMapper.toDBEntity(promotion));
    }

    @Override
    public Promotion get(Long promotionId) {
        return PromotionMapper.toModel(promotionDao.get(promotionId));
    }

    @Override
    public Long update(Promotion promotion) {
        PromotionEntity dbEntity = promotionDao.get(promotion.getPromotionId());
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("offer", promotion.getPromotionId()).exception();
        }
        PromotionMapper.fillDBEntity(promotion, dbEntity);
        return promotionDao.update(dbEntity);
    }

    @Override
    public void delete(Long promotionId) {
        PromotionEntity dbEntity = promotionDao.get(promotionId);
        if (dbEntity == null) {
            throw AppErrors.INSTANCE.notFound("promotion", promotionId).exception();
        }
        dbEntity.setDeleted(true);
        promotionDao.update(dbEntity);
    }
}
