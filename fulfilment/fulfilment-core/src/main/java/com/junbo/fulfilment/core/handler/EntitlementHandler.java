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
        entitlement.setOfferId(context.getItems().get(action.getFulfilmentId()).getOfferId());

        // fetch from entitlement definition
        entitlement.setUseCount(action.getCopyCount());
        entitlement.setType((String) prop.get(Constant.ENTITLEMENT_TYPE));
        entitlement.setTag((String) prop.get(Constant.ENTITLEMENT_TAG));
        entitlement.setGroup((String) prop.get(Constant.ENTITLEMENT_GROUP));
        entitlement.setDeveloperId((Long) prop.get(Constant.ENTITLEMENT_DEVELOPER));
        entitlement.setEntitlementDefinitionId((Long) prop.get(Constant.ENTITLEMENT_DEF_ID));
        entitlement.setGrantDate(Utils.now());

        return entitlementGateway.grant(entitlement);
    }
}
