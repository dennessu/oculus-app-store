/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Promotion service definition.
 */
@Transactional
public interface PromotionService extends BaseRevisionedService<Promotion, PromotionRevision> {
    List<Promotion> getEffectivePromotions(PromotionsGetOptions options);
    List<PromotionRevision> getRevisions(PromotionRevisionsGetOptions options);
}
