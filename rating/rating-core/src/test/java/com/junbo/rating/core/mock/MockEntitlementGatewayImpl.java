/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.mock;

import com.junbo.rating.clientproxy.EntitlementGateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lizwu on 3/28/14.
 */
public class MockEntitlementGatewayImpl implements EntitlementGateway {
    @Override
    public Map<Long, Long> getEntitlements(Long userId, Set<Long> entitlementDefinitionIds) {
        return new HashMap<Long, Long>() {{
            put(400L, 500L);
        }};
    }
}