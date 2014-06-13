/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import com.junbo.catalog.spec.resource.PromotionRevisionResource;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Promotion revision resource implementation.
 */
public class PromotionRevisionResourceImpl implements PromotionRevisionResource {
    @Autowired
    private PromotionService promotionService;

    @Override
    public Promise<Results<PromotionRevision>> getPromotionRevisions(PromotionRevisionsGetOptions options) {
        List<PromotionRevision> revisions = promotionService.getRevisions(options);
        Results<PromotionRevision> results = new Results<>();
        results.setItems(revisions);
        return Promise.pure(results);
    }

    @Override
    public Promise<PromotionRevision> getPromotionRevision(String revisionId) {
        return Promise.pure(promotionService.getRevision(revisionId));
    }

    @Override
    public Promise<PromotionRevision> createPromotionRevision(PromotionRevision promotionRevision) {
        return Promise.pure(promotionService.createRevision(promotionRevision));
    }

    @Override
    public Promise<PromotionRevision> updatePromotionRevision(String revisionId,
                                                              PromotionRevision promotionRevision) {
        return Promise.pure(promotionService.updateRevision(revisionId, promotionRevision));
    }
}
