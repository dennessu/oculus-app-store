/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.mock;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.subscription.clientproxy.impl.EntitlementGatewayImpl;

/**
 * Created by Administrator on 14-4-1.
 */
public class MockEntitlementGatewayImpl extends EntitlementGatewayImpl {
    @Override
    public String grantEntitlement(Entitlement entitlement){
        return "12345L";
    }
}
