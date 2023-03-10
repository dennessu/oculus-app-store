/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy;

import java.util.Set;

/**
 * Entitlement gateway interface.
 */
public interface EntitlementGateway {
    Set<String> getEntitlements(Long userId, Set<String> itemIds);
}
