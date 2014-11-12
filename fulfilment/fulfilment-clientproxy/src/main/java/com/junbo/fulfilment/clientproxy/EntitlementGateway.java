/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.fulfilment.spec.fusion.Entitlement;

import java.util.List;
import java.util.Map;

/**
 * EntitlementGateway.
 */
public interface EntitlementGateway {
    Map<Long, List<String>> grant(Map<Long, List<Entitlement>> entitlements);
}
