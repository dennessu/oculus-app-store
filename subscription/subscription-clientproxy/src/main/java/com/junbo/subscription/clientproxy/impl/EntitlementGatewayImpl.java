/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.impl;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.subscription.clientproxy.EntitlementGateway;
import com.junbo.subscription.common.exception.SubscriptionExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;

/**
 * Created by Administrator on 14-3-27.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementGatewayImpl.class);

    @Autowired
    @Qualifier("entitlementClient")
    private EntitlementResource entitlementResource;

    @Override
    public String grantEntitlement(Entitlement entitlement) {
        try  {
            entitlement.setTrackingUuid(UUID.randomUUID());

            Entitlement result = entitlementResource.postEntitlement(entitlement).get();
            return result.getId();
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Entitlement] component.", e);
            throw SubscriptionExceptions.INSTANCE.gatewayFailure("entitlement").exception();
        }

    }

}
