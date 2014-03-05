/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.EntitlementCriterion;
import com.junbo.rating.core.context.RatingContext;

/**
 * Created by lizwu on 2/21/14.
 */
public class EntitlementCriterionHandler implements CriterionHandler<EntitlementCriterion> {
    @Override
    public boolean validate(EntitlementCriterion criterion, RatingContext context) {
        //call entitlement to get owned entitlement

        return false;
    }
}
