/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.core.context.EntitlementContext;
import com.junbo.fulfilment.spec.fusion.Entitlement;
import com.junbo.fulfilment.spec.fusion.EntitlementMeta;
import com.junbo.fulfilment.spec.fusion.Item;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EntitlementHandler.
 */
public class EntitlementHandler extends HandlerSupport<EntitlementContext> {
    @Override
    protected Object handle(EntitlementContext context, FulfilmentAction action) {
        Map<String, Object> prop = action.getProperties();
        List<String> results = new ArrayList<>();

        Item item = catalogGateway.getItem(action.getItemId(), action.getTimestamp());

        for (int i = 0; i < action.getCopyCount(); i++) {
            for (EntitlementMeta meta : item.getEntitlementMetas()) {
                Entitlement entitlement = new Entitlement();

                entitlement.setType(meta.getType());
                entitlement.setItemId(action.getItemId());
                entitlement.setUserId(context.getUserId());

                // for non-consumable, always null
                // for consumable, get use count from TBD, hard code 1 as workaround for now
                entitlement.setUseCount(meta.getConsumable() ? 1 : null);
                entitlement.setGrantTime(Utils.now());

                results.add(entitlementGateway.grant(entitlement));
            }
        }

        return results;
    }
}
