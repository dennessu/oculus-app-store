/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.User;
import com.junbo.identity.spec.resource.v1.resource.UserResource;
import com.junbo.identity.spec.v1.model.options.UserGetOption;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
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
