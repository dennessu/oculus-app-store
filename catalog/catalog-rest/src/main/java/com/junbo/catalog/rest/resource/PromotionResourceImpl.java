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
import com.junbo.common.id.Id;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;

/**
 * Promotion resource implementation.
 */
public class PromotionResourceImpl extends BaseResourceImpl<Promotion> implements PromotionResource{
    @Autowired
    private PromotionService promotionService;

    @Override
    public Promise<ResultList<Promotion>> getPromotions(@BeanParam EntitiesGetOptions options) {
        return getEntities(options);
    }

    @Override
    public Promise<Promotion> getPromotion(Id promotionId, @BeanParam EntityGetOptions options) {
        return get(promotionId, options);
    }

    @Override
    protected PromotionService getEntityService() {
        return promotionService;
    }
}
