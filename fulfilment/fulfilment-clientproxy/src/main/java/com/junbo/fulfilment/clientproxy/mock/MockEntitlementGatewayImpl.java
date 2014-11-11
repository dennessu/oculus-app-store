/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.fulfilment.clientproxy.EntitlementGateway;
import com.junbo.fulfilment.spec.fusion.Entitlement;

import java.util.Collections;
import java.util.List;

/**
 * MockEntitlementGatewayImpl.
 */
public class MockEntitlementGatewayImpl implements EntitlementGateway {
    @Override
    public List<String> grant(List<Entitlement> entitlement) {
        return Collections.singletonList("12345L");
    }
}
