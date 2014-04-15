/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;

import java.util.List;

/**
 * Promotion DAO definition.
 */
public interface PromotionDao extends BaseDao<PromotionEntity> {
    List<PromotionEntity> getEffectivePromotions(PromotionsGetOptions options);
}
