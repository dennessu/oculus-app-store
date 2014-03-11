/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.identity.impl;

import com.junbo.common.id.UserId;
import com.junbo.entitlement.clientproxy.identity.UserFacade;
import com.junbo.identity.spec.resource.proxy.UserResourceClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Impl of UserFacade.
 */
public class UserFacadeImpl implements UserFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);
    @Autowired
    private UserResourceClientProxy identityUserClient;

    @Override
    public boolean exists(Long userId) {
        try {
            LOGGER.info("Getting user [{}] started.", userId);
            identityUserClient.getUser(new UserId(userId)).wrapped().get();
            LOGGER.info("Getting user [{}] finished.", userId);
        } catch (Exception e) {
            LOGGER.error("Getting user [{" + userId + "}] failed.", e);
            return false;
        }
        return true;
    }
}
