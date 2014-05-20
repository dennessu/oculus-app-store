/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.criterion.CouponCriterion;
import com.junbo.rating.core.context.RatingContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lizwu on 2/21/14.
 */
public class CouponCriterionHandler implements CriterionHandler<CouponCriterion> {
    @Override
    public boolean validate(CouponCriterion criterion, RatingContext context) {
        Set<String> couponCodes = context.getCouponCodes().keySet();

        //TODO: call token to get couponClasses
        Set<String> couponClassSet = new HashSet<String>();

        return couponClassSet.contains(criterion.getCouponClass());
    }
}
