/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.PromotionService;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.resource.PromotionRevisionResource;
import com.junbo.common.id.PromotionId;
import com.junbo.common.id.PromotionRevisionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Promotion revision resource implementation.
 */
public class PromotionRevisionResourceImpl implements PromotionRevisionResource {
    @Autowired
    private PromotionService promotionService;

    @Override
    public Promise<Results<PromotionRevision>> getPromotionRevisions(PromotionId promotionId) {
        return null;
    }

    @Override
    public Promise<PromotionRevision> getPromotionRevision(PromotionRevisionId revisionId) {
        return Promise.pure(promotionService.getRevision(revisionId.getValue()));
    }

    @Override
    public Promise<PromotionRevision> createPromotionRevision(PromotionRevision promotionRevision) {
        return Promise.pure(promotionService.createRevision(promotionRevision));
    }

    @Override
    public Promise<PromotionRevision> updatePromotionRevision(PromotionRevisionId revisionId,
                                                              PromotionRevision promotionRevision) {
        return Promise.pure(promotionService.updateRevision(revisionId.getValue(), promotionRevision));
    }
}
