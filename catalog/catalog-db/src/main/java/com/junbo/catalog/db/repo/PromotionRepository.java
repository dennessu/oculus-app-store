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
public class PromotionRepository {
    @Autowired
    private PromotionDao promotionDao;

    public Long create(Promotion promotion) {
        PromotionEntity entity = PromotionConverter.toEntity(promotion);

        return promotionDao.create(entity);
    }

    public Promotion get(Long offerId, Integer revision) {
        PromotionEntity entity = promotionDao.getPromotion(offerId, revision);
        return PromotionConverter.toModel(entity);
    }
}
