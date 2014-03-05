/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.clientproxy.identity;

/**
 * Interface wrapper to call from Identity.
 */
public interface UserFacade {
    boolean exists(Long userId);
}
