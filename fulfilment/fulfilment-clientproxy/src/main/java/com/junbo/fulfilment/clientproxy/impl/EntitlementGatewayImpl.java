/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.fulfilment.clientproxy.EntitlementGateway;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.error.AppErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;

/**
 * EntitlementGatewayImpl.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementGatewayImpl.class);

    @Autowired
    @Qualifier("entitlementClient")
    private EntitlementResource entitlementResource;

    @Override
    public String grant(com.junbo.fulfilment.spec.fusion.Entitlement input) {
        try {
            Entitlement entitlement = Utils.map(input, Entitlement.class);
            entitlement.setTrackingUuid(UUID.randomUUID());

            Entitlement result = entitlementResource.postEntitlement(entitlement).syncGet();
            return result.getId();
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Entitlement] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("Entitlement").exception();
        }
    }
}
