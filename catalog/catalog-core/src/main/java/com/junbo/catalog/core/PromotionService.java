/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.promotion.Promotion;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Promotion service definition.
 */
@Transactional
public interface PromotionService {
    Promotion createPromotion(Promotion promotion);
    //Promotion updatePromotion(Promotion updatedPromotion);
    Promotion getPromotion(Long promotionId, EntityGetOptions options);
    List<Promotion> getEffectivePromotions(int page, int size);
}
