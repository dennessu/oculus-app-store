/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.Entitlement;
import com.junbo.catalog.spec.model.promotion.EntitlementCriterion;
import com.junbo.rating.clientproxy.EntitlementGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.RatingContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lizwu on 2/21/14.
 */
public class EntitlementCriterionHandler implements CriterionHandler<EntitlementCriterion> {
    @Autowired
    private EntitlementGateway entitlementGateway;

    @Override
    public boolean validate(EntitlementCriterion criterion, RatingContext context) {
        Set<String> groups = new HashSet<>();

        for (Entitlement entitlement : criterion.getEntitlements()) {
            groups.add(entitlement.getGroup());
        }

        //call entitlement to get owned entitlement
        Set<String> entitlements = entitlementGateway.getEntitlements(context.getUserId(), groups);

        for (Entitlement entitlement : criterion.getEntitlements()) {
            switch (criterion.getPredicate()) {
                case INCLUDE_ENTITLEMENT:
                    if (!entitlements.contains(
                            entitlement.getGroup() + Constants.ENTITLEMENT_SEPARATOR + entitlement.getTag())) {
                        return false;
                    }
                    break;
                case EXCLUDE_ENTITLEMENT:
                    if (entitlements.contains(
                            entitlement.getGroup() + Constants.ENTITLEMENT_SEPARATOR + entitlement.getTag())) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }

        return true;
    }
}
