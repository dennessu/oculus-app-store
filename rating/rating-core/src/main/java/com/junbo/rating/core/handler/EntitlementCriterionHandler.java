/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.google.common.collect.Sets;
import com.junbo.catalog.spec.model.promotion.EntitlementCriterion;
import com.junbo.rating.clientproxy.EntitlementGateway;
import com.junbo.rating.core.context.RatingContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by lizwu on 2/21/14.
 */
public class EntitlementCriterionHandler implements CriterionHandler<EntitlementCriterion> {
    @Autowired
    private EntitlementGateway entitlementGateway;

    @Override
    public boolean validate(EntitlementCriterion criterion, RatingContext context) {
        //anonymous user
        if(context.getUserId() == null){
            return false;
        }

        Set<Long> entitlements =
                entitlementGateway.getEntitlements(context.getUserId(), Sets.newHashSet(criterion.getItems()));

        switch(criterion.getPredicate()) {
            case INCLUDE_ENTITLEMENT:
                if (Collections.disjoint(entitlements, criterion.getItems())) {
                    return false;
                }
                break;
            case EXCLUDE_ENTITLEMENT:
                if (!Collections.disjoint(entitlements, criterion.getItems())) {
                    return false;
                }
        }

        return true;
    }
}
