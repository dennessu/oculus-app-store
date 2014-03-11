/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.PromotionEntity;

/**
 * Promotion DAO definition.
 */
public interface PromotionDao extends BaseDao<PromotionEntity> {
    PromotionEntity getPromotion(Long promotionId, Long timestamp);
    //List<PromotionEntity> getPromotions(long promotionId, int start, int size);
}
