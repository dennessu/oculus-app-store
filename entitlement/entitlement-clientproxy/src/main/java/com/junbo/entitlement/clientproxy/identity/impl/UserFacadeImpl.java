/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.identity.impl;

import com.junbo.entitlement.clientproxy.identity.UserFacade;
import com.junbo.identity.spec.model.user.User;
import com.junbo.identity.spec.resource.proxy.UserResourceClientProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * Impl of UserFacade.
 */
public class UserFacadeImpl implements UserFacade {
    @Autowired
    private UserResourceClientProxy identityUserClient;

    @Override
    public boolean exists(Long userId) {
        User user = null;
        try {
            user = identityUserClient.getUser(userId).wrapped().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (user == null) {
            return false;
        }
        return true;
    }
}
