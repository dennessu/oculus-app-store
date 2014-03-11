/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.convertor.PromotionConverter;
import com.junbo.catalog.db.dao.PromotionDao;
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
        return promotionDao.create(PromotionConverter.toEntity(promotion));
    }

    @Override
    public Promotion get(Long promotionId, Long timestamp) {
        return PromotionConverter.toModel(promotionDao.getPromotion(promotionId, timestamp));
    }
}
