/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.identity.impl;

import com.junbo.entitlement.clientproxy.identity.UserFacade;

/**
 * A mock impl of UserFacade.
 */
public class MockedUserFacadeImpl implements UserFacade {
    @Override
    public boolean exists(Long userId) {
        return true;
    }
}
