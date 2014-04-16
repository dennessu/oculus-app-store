/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.identity.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.resource.UserResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.clientproxy.identity.IdentityFacade;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by linyi on 14-2-19.
 */
@Component("orderIdentityFacade")
public class IdentityFacadeImpl implements IdentityFacade {
    @Resource(name="order.identityUserClient")
    private UserResource userResource;

    public void setUserResource(UserResource userResource) {
        this.userResource = userResource;
    }

    @Override
    public Promise<User> getUser(Long userId) {
        return userResource.get(new UserId(userId), null);
    }

    @Override
    public Promise<User> createUser(User user) {
        Promise<User> userPromise = null;

        try {
            userPromise = userResource.create(user);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return userPromise;
    }
}
