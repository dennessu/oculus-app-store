/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.resource.UserEntitlementResource;
import com.junbo.rating.clientproxy.EntitlementGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.spec.error.AppErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Entitlement gateway.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    @Autowired
    @Qualifier("ratingUserEntitlementClient")
    private UserEntitlementResource userEntitlementResource;

    @Override
    public Map<Long, Long> getEntitlements(Long userId, Set<Long> definitionIds) {
        Set<EntitlementDefinitionId> entitlementDefinitionIds = new HashSet<>();
        for (Long definitionId : definitionIds) {
            entitlementDefinitionIds.add(new EntitlementDefinitionId(definitionId));
        }
        EntitlementSearchParam param = new EntitlementSearchParam();
        param.setDefinitionIds(entitlementDefinitionIds);

        PageMetadata pagingOption = new PageMetadata();
        pagingOption.setStart(Constants.DEFAULT_PAGE_START);
        pagingOption.setCount(Constants.DEFAULT_PAGE_SIZE);

        Map<Long, Long> result = new HashMap<>();
        while(true) {
            List<Entitlement> entitlements = new ArrayList<Entitlement>();
            try {
                entitlements.addAll(
                        userEntitlementResource.getEntitlements(
                                new UserId(userId), param, pagingOption).wrapped().get().getItems());
            } catch (Exception e) {
                throw AppErrors.INSTANCE.entitlementGatewayError().exception();
            }
            for (Entitlement entitlement : entitlements) {
                result.put(entitlement.getEntitlementDefinitionId(), entitlement.getEntitlementId());
            }
            pagingOption.setStart(pagingOption.getStart() + Constants.DEFAULT_PAGE_SIZE);
            if (entitlements.size() < Constants.DEFAULT_PAGE_SIZE) {
                break;
            }
        }

        return result;
    }
}
