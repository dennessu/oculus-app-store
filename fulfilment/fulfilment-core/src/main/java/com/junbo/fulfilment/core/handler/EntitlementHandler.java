/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.common.util.Callback;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.core.FulfilmentHandler;
import com.junbo.fulfilment.core.context.EntitlementContext;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.fusion.Entitlement;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

/**
 * EntitlementHandler.
 */
public class EntitlementHandler extends HandlerSupport implements FulfilmentHandler<EntitlementContext> {
    private static final String ENTITLMENT_GROUP = "GROUP";
    private static final String ENTITLMENT_TAG = "TAG";
    private static final String ENTITLMENT_TYPE = "TYPE";

    @Override
    public void process(EntitlementContext context) {
        for (final FulfilmentAction action : context.getActions()) {
            try {
                action.setResult(grant(context, action));
                action.setStatus(FulfilmentStatus.SUCCEED);
            } catch (Exception e) {
                action.setStatus(FulfilmentStatus.FAILED);
            }

            executeInNewTransaction(new Callback() {
                public void apply() {
                    updateAction(action.getActionId(), action.getStatus(), action.getResult());
                }
            });
        }
    }

    private String grant(EntitlementContext context, FulfilmentAction action) {
        Entitlement entitlement = new Entitlement();

        entitlement.setType(action.getProperties().get(ENTITLMENT_TYPE));
        entitlement.setGroup(action.getProperties().get(ENTITLMENT_GROUP));
        entitlement.setTag(action.getProperties().get(ENTITLMENT_TAG));
        entitlement.setGrantDate(Utils.now());
        entitlement.setUserId(context.getUserId());
        entitlement.setOfferId(context.getItems().get(action.getFulfilmentId()).getOfferId());

        return entitlementGateway.grant(entitlement);
    }
}
