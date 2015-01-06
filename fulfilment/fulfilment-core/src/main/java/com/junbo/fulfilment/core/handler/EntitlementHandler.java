/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.core.context.EntitlementContext;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.fusion.Entitlement;
import com.junbo.fulfilment.spec.fusion.EntitlementMeta;
import com.junbo.fulfilment.spec.fusion.Item;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EntitlementHandler.
 */
public class EntitlementHandler extends HandlerSupport<EntitlementContext> {

    @Override
    public void process(EntitlementContext context) {
        LOGGER.info("Start preparing entitlements for order [" + context.getOrderId() + "].");
        Map<Long, List<Entitlement>> prepared = new HashMap<>();
        try {
            for (FulfilmentAction action : context.getActions()) {
                prepared.put(action.getActionId(), buildEntitlements(context, action));
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred during preparing action.", e);
            setFulfilmentResults(context, FulfilmentStatus.FAILED, null);
            return;
        }

        LOGGER.info("Start granting entitlements for order [" + context.getOrderId() + "].");
        try {
            Map<Long, List<String>> results = entitlementGateway.grant(prepared);
            LOGGER.info("Finish granting entitlements for order [" + context.getOrderId() + "].");
            setFulfilmentResults(context, FulfilmentStatus.SUCCEED, results);
        } catch (Exception e) {
            LOGGER.error("Error occurred during granting entitlements for order [" + context.getOrderId() + "].", e);
            setFulfilmentResults(context, FulfilmentStatus.FAILED, null);
        }
    }

    private void setFulfilmentResults(EntitlementContext context, String status, Map<Long, List<String>> results) {
        for (FulfilmentAction action : context.getActions()) {
            if (results != null) {
                FulfilmentResult fulfilmentResult = new FulfilmentResult();
                fulfilmentResult.setEntitlementIds(results.get(action.getActionId()));
                actionRepo.update(action.getActionId(), status, Utils.toJson(fulfilmentResult));
            } else {
                actionRepo.update(action.getActionId(), status, null);
            }
        }
    }

    private List<Entitlement> buildEntitlements(EntitlementContext context, FulfilmentAction action) {
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
        return entitlements;
    }
}
