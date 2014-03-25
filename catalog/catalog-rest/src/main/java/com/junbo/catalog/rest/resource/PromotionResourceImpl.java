/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import com.junbo.catalog.spec.resource.PromotionResource;
import com.junbo.common.id.PromotionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Promotion resource implementation.
 */
public class PromotionResourceImpl extends BaseResourceImpl<Promotion> implements PromotionResource{
    @Autowired
    private PromotionService promotionService;

    @Override
    public Promise<Results<Promotion>> getPromotions(PromotionsGetOptions options) {
        return getEntities(options);
    }

    @Override
    public Promise<Promotion> getPromotion(PromotionId promotionId, EntityGetOptions options) {
        return get(promotionId, options);
    }

    @Override
    public Promise<Promotion> update(PromotionId promotionId, Promotion promotion) {
        return super.update(promotionId, promotion);
    }

    @Override
    protected PromotionService getEntityService() {
        return promotionService;
    }
}
