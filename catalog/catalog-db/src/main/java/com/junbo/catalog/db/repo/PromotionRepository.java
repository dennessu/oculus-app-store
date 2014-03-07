/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.PromotionConverter;
import com.junbo.catalog.db.dao.PromotionDao;
import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.spec.model.promotion.Promotion;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Promotion repository.
 */
public class PromotionRepository implements EntityRepository<Promotion> {
    @Autowired
    private PromotionDao promotionDao;

    @Override
    public Long create(Promotion promotion) {
        PromotionEntity entity = PromotionConverter.toEntity(promotion);

        return promotionDao.create(entity);
    }

    @Override
    public Promotion get(Long offerId, Long timestamp) {
        //PromotionEntity entity = promotionDao.getPromotion(offerId, timestamp);
        //return PromotionConverter.toModel(entity);
        return null;
    }
}
