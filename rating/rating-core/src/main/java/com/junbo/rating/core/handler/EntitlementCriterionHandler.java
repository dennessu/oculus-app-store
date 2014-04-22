/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.Entitlement;
import com.junbo.catalog.spec.model.promotion.EntitlementCriterion;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.clientproxy.EntitlementGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.RatingContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by lizwu on 2/21/14.
 */
public class EntitlementCriterionHandler implements CriterionHandler<EntitlementCriterion> {
    @Autowired
    private CatalogGateway catalogGateway;

    @Autowired
    private EntitlementGateway entitlementGateway;

    @Override
    public boolean validate(EntitlementCriterion criterion, RatingContext context) {
        //anonymous user
        if(context.getUserId() == null){
            return false;
        }

        Set<String> groups = new HashSet<>();
        Set<String> entitlementStrs = new HashSet<>();
        for (Entitlement entitlement : criterion.getEntitlements()) {
            groups.add(entitlement.getGroup());
            entitlementStrs.add(entitlement.getGroup() + Constants.ENTITLEMENT_SEPARATOR + entitlement.getTag());
        }

        //get entitlement definitions by groups from catalog service
        //key: entitlement definition id
        //value: group#tag
        Map<Long, String> entitlementDefinitions = catalogGateway.getEntitlementDefinitions(groups);

        //filter entitlement definitions by group & tag pair
        Iterator<Map.Entry<Long, String>> itr = entitlementDefinitions.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<Long, String> entry = itr.next();
            if (!entitlementStrs.contains(entry.getValue())) {
                itr.remove();
            }
        }

        //return false if no entitlement definition is found
        if (entitlementDefinitions.keySet().size() <= 0) {
            return false;
        }

        //get entitlements by user id and entitlement definition ids from entitlement service
        //key: entitlement definition id
        //value: entitlement id
        Map<Long, Long> entitlements =
                entitlementGateway.getEntitlements(context.getUserId(), entitlementDefinitions.keySet());

        Set<String> effectiveEntitlements = join(entitlementDefinitions, entitlements);

        switch(criterion.getPredicate()) {
            case INCLUDE_ENTITLEMENT:
                if (Collections.disjoint(entitlementStrs, effectiveEntitlements)) {
                    return false;
                }
                break;
            case EXCLUDE_ENTITLEMENT:
                if (!Collections.disjoint(entitlementStrs, effectiveEntitlements)) {
                    return false;
                }
        }

        return true;
    }

    private Set<String> join(Map<Long, String> definitions, Map<Long, Long> entitlements) {
        Set<String> result = new HashSet<>();
        for (Long definitionId : definitions.keySet()) {
            if (entitlements.containsKey(definitionId)) {
                result.add(definitions.get(definitionId));
            }
        }

        return result;
    }
}
