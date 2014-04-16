/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import com.junbo.catalog.spec.resource.PromotionResource;
import com.junbo.common.id.PromotionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Promotion resource implementation.
 */
public class PromotionResourceImpl implements PromotionResource{
    @Autowired
    private PromotionService promotionService;

    @Override
    public Promise<Results<Promotion>> getPromotions(PromotionsGetOptions options) {
        List<Promotion> promotions = promotionService.getEffectivePromotions(options);
        Results<Promotion> results = new Results<>();
        results.setItems(promotions);
        return Promise.pure(results);
    }

    @Override
    public Promise<Promotion> getPromotion(PromotionId promotionId) {
        return Promise.pure(promotionService.getEntity(promotionId.getValue()));
    }

    @Override
    public Promise<Promotion> create(Promotion promotion) {
        return Promise.pure(promotionService.createEntity(promotion));
    }

    @Override
    public Promise<Promotion> update(PromotionId promotionId, Promotion promotion) {
        return Promise.pure(promotionService.updateEntity(promotionId.getValue(), promotion));
    }

    @Override
    public Promise<Response> delete(PromotionId promotionId) {
        promotionService.deleteEntity(promotionId.getValue());
        return Promise.pure(Response.status(204).build());
    }
}
