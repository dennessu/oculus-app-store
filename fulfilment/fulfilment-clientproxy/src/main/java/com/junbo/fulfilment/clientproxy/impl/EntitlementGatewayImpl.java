/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.fulfilment.clientproxy.EntitlementGateway;
import com.junbo.fulfilment.common.exception.EntitlementGatewayException;
import com.junbo.fulfilment.common.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * EntitlementGatewayImpl.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    @Autowired
    private EntitlementResource entitlementResource;


    @Override
    public String grant(com.junbo.fulfilment.spec.fusion.Entitlement input) {
        try {
            Entitlement entitlement = Utils.map(input, Entitlement.class);
            Entitlement result = entitlementResource.postEntitlement(entitlement).wrapped().get();

            return result.getEntitlementId().toString();
        } catch (Exception e) {
            throw new EntitlementGatewayException("Error occurred during calling [Entitlement] component service.", e);
        }
    }
}
