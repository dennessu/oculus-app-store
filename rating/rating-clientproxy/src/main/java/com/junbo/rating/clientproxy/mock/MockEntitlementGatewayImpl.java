/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.mock;

import com.junbo.rating.clientproxy.EntitlementGateway;

import java.util.Collections;
import java.util.Set;

/**
 * Created by lizwu on 3/6/14.
 */
public class MockEntitlementGatewayImpl implements EntitlementGateway{
    @Override
    public Set<String> getEntitlements(Long userId, Set<String> groups) {
        return Collections.emptySet();
    }
}
