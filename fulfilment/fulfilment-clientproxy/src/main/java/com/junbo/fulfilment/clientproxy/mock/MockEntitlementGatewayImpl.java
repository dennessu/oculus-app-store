/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.fulfilment.clientproxy.EntitlementGateway;
import com.junbo.fulfilment.spec.fusion.Entitlement;

import java.util.List;
import java.util.Map;

/**
 * MockEntitlementGatewayImpl.
 */
public class MockEntitlementGatewayImpl implements EntitlementGateway {
    @Override
    public Map<Long, List<String>> grant(Map<Long, List<Entitlement>> entitlement) {
        return null;
    }

    @Override
    public boolean revokeNonConsumable(String entitlementId) {
        return false;
    }

    @Override
    public boolean revokeConsumable(String entitlementId, Integer count) {
        return false;
    }
}
