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

import java.util.Map;

/**
 * EntitlementHandler.
 */
public class EntitlementHandler extends HandlerSupport<EntitlementContext> {
    @Override
    protected String handle(EntitlementContext context, FulfilmentAction action) {
        Entitlement entitlement = new Entitlement();

        Map<String, Object> prop = action.getProperties();

        entitlement.setUserId(context.getUserId());
        //entitlement.setUseCount(action.getCopyCount());
        entitlement.setEntitlementDefinitionId((Long) prop.get(Constant.ENTITLEMENT_DEF_ID));
        entitlement.setGrantTime(Utils.now());

        return entitlementGateway.grant(entitlement);
    }
}
