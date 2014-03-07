/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.db.repo.PromotionDraftRepository;
import com.junbo.catalog.db.repo.PromotionRepository;
import com.junbo.catalog.spec.model.promotion.Promotion;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Promotion service implementation.
 */
public class PromotionServiceImpl extends BaseServiceImpl<Promotion> implements PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionDraftRepository promotionDraftRepository;

    @Override
    public PromotionRepository getEntityRepo() {
        return promotionRepository;
    }

    @Override
    public PromotionDraftRepository getEntityDraftRepo() {
        return promotionDraftRepository;
    }
}
