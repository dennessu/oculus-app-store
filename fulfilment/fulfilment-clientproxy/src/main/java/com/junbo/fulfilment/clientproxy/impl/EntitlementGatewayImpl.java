/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.RevokeRequest;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.fulfilment.clientproxy.EntitlementGateway;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.error.AppErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * EntitlementGatewayImpl.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementGatewayImpl.class);

    @Autowired
    @Qualifier("entitlementClient")
    private EntitlementResource entitlementResource;

    @Override
    public Map<Long, List<String>> grant(Map<Long, List<com.junbo.fulfilment.spec.fusion.Entitlement>> input) {
        Map<Long, List<Entitlement>> entitlementResult = new HashMap<>();
        for (Long actionId : input.keySet()) {
            List<Entitlement> entitlements = new ArrayList<>(input.size());
            for (com.junbo.fulfilment.spec.fusion.Entitlement e : input.get(actionId)) {
                Entitlement mapped = Utils.map(e, Entitlement.class);
                mapped.setTrackingUuid(UUID.randomUUID());
                entitlements.add(mapped);
            }
            entitlementResult.put(actionId, entitlements);
        }

        try {
            entitlementResult = entitlementResource.postEntitlements(entitlementResult).get();
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Entitlement] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("Entitlement").exception();
        }

        Map<Long, List<String>> result = new HashMap<>();
        for (Long actionId : entitlementResult.keySet()) {
            List<String> entitlementIds = new LinkedList<>();
            for (Entitlement entitlement : entitlementResult.get(actionId)) {
                entitlementIds.add(entitlement.getId());
            }
            result.put(actionId, entitlementIds);
        }
        return result;
    }

    @Override
    public boolean revokeNonConsumable(String entitlementId) {
        return revokeConsumable(entitlementId, null);
    }

    @Override
    public boolean revokeConsumable(String entitlementId, Integer count) {
        try {
            RevokeRequest revokeRequest = new RevokeRequest();
            revokeRequest.setEntitlementId(entitlementId);
            revokeRequest.setCount(count);

            Response response = entitlementResource.revokeEntitlement(revokeRequest).get();

            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Entitlement] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("Entitlement").exception();
        }
    }
}
