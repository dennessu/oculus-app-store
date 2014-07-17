/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.rating.clientproxy.EntitlementGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.spec.error.AppErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entitlement gateway.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementGatewayImpl.class);

    @Autowired
    @Qualifier("ratingEntitlementClient")
    private EntitlementResource entitlementResource;

    @Override
    public Set<String> getEntitlements(Long userId, Set<String> itemIds) {
        Set<ItemId> items = new HashSet<>();
        for (String itemId : itemIds) {
            items.add(new ItemId(itemId));
        }
        EntitlementSearchParam param = new EntitlementSearchParam();
        param.setUserId(new UserId(userId));
        param.setItemIds(items);

        PageMetadata pagingOption = new PageMetadata();
        pagingOption.setStart(Constants.DEFAULT_PAGE_START);
        pagingOption.setCount(Constants.DEFAULT_PAGE_SIZE);

        Set<String> result = new HashSet<>();
        while (true) {
            List<Entitlement> entitlements = new ArrayList<Entitlement>();
            try {
                entitlements.addAll(
                        entitlementResource.searchEntitlements(
                                param, pagingOption).get().getItems()
                );
            } catch (Exception e) {
                LOGGER.error("Error occurred during calling [Entitlement] component.", e);
                throw AppErrors.INSTANCE.entitlementGatewayError().exception();
            }

            for (Entitlement entitlement : entitlements) {
                result.add(entitlement.getItemId());
            }

            pagingOption.setStart(pagingOption.getStart() + Constants.DEFAULT_PAGE_SIZE);
            if (entitlements.size() < Constants.DEFAULT_PAGE_SIZE) {
                break;
            }
        }

        return result;
    }
}
