/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.resource.PromotionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Promotion resource implementation.
 */
public class PromotionResourceImpl implements PromotionResource{
    @Autowired
    private PromotionService promotionService;

    @POST
    public Promise<Promotion> createPromotion(Promotion promotion) {
        Promotion newPromotion = promotionService.create(promotion);
        return Promise.pure(newPromotion);
    }

    @Override
    public Promise<Promotion> getPromotion(Long promotionId, EntityGetOptions options) {
        Promotion promotion = promotionService.get(promotionId, options);
        return Promise.pure(promotion);
    }

    @Override
    public Promise<ResultList<Promotion>> getPromotions(EntitiesGetOptions options) {
        List<Promotion> promotions;
        if (options.getEntityIds() != null && options.getEntityIds().size() > 0) {
            promotions = new ArrayList<>();
            for (Long promotionId : options.getEntityIds()) {
                promotions.add(promotionService.get(promotionId, EntityGetOptions.getDefault()));
            }
        } else {
            options.ensurePagingValid();
            promotions = promotionService.getEntities(options);
        }
        ResultList<Promotion> resultList = new ResultList<>();
        resultList.setResults(promotions);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }
}
