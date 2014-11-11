/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.core.context.EntitlementContext;
import com.junbo.fulfilment.spec.fusion.Entitlement;
import com.junbo.fulfilment.spec.fusion.EntitlementMeta;
import com.junbo.fulfilment.spec.fusion.Item;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * EntitlementHandler.
 */
public class EntitlementHandler extends HandlerSupport<EntitlementContext> {
    @Override
    protected FulfilmentResult handle(EntitlementContext context, FulfilmentAction action) {
        List<String> entitlementIds;

        Item item = catalogGateway.getItem(action.getItemId(), action.getTimestamp());

        List<Entitlement> entitlements = new ArrayList<>();
        for (int i = 0; i < action.getCopyCount(); i++) {
            for (EntitlementMeta meta : item.getEntitlementMetas()) {
                Entitlement entitlement = new Entitlement();

                entitlement.setType(meta.getType());
                entitlement.setItemId(action.getItemId());
                entitlement.setUserId(context.getUserId());

                // for non-consumable, always null
                // for consumable, get use count from action property
                entitlement.setUseCount(meta.getConsumable()
                        ? (Integer) action.getProperties().get(Constant.USE_COUNT) : null);

                entitlement.setGrantTime(Utils.now());
                entitlements.add(entitlement);
            }
        }

        entitlementIds = entitlementGateway.grant(entitlements);

        FulfilmentResult result = new FulfilmentResult();
        result.setEntitlementIds(entitlementIds);

        return result;
    }
}
