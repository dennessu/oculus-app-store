/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.identity.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.user.User;
import com.junbo.identity.spec.resource.UserResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.clientproxy.identity.IdentityFacade;
import groovy.transform.CompileStatic;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by linyi on 14-2-19.
 */
@Component("orderIdentityFacade")
@CompileStatic
public class IdentityFacadeImpl implements IdentityFacade {
    @Resource(name="order.identityUserClient")
    private UserResource userResource;

    public void setUserResource(UserResource userResource) {
        this.userResource = userResource;
    }

    @Override
    public Promise<User> getUser(Long userId) {
        Promise<User> userPromise = null;

        try {
            userPromise = userResource.getUser(new UserId(userId));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return userPromise;
    }

    @Override
    public Promise<User> createUser(User user) {
        Promise<User> userPromise = null;

        try {
            userPromise = userResource.postUser(user);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return userPromise;
    }
}
