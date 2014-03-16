/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.options.UserGetOption;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.resource.UserResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserResourceImpl implements UserResource {
    @Override
    public Promise<User> create(User user) {
        return null;
    }

    @Override
    public Promise<User> update(UserId userId, User user) {
        return null;
    }

    @Override
    public Promise<User> patch(UserId userId, User user) {
        return null;
    }

    @Override
    public Promise<User> get(UserId userId) {
        return null;
    }

    @Override
    public Promise<ResultList<User>> list(@BeanParam UserGetOption getOption) {
        return null;
    }
}
