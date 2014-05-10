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

        for (int i = 0; i < action.getCopyCount(); i++) {
            Entitlement entitlement = new Entitlement();

            entitlement.setUserId(context.getUserId());
            entitlement.setUseCount((Integer) (prop.get(Constant.USE_COUNT)));
            entitlement.setEntitlementDefinitionId((Long) prop.get(Constant.ENTITLEMENT_DEF_ID));
            entitlement.setGrantTime(Utils.now());

            results.add(entitlementGateway.grant(entitlement));
        }

        return results;
    }
}
