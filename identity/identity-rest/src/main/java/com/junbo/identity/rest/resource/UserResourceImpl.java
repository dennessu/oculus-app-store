/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.identity.core.service.user.UserService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.options.entity.UserGetOptions;
import com.junbo.identity.spec.options.list.UserListOption;
import com.junbo.identity.spec.resource.UserResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserService userService;

    @Override
    public Promise<User> create(User user) {
        return Promise.pure(userService.save(user));
    }

    @Override
    public Promise<User> put(UserId userId, User user) {
        return Promise.pure(userService.update(userId, user));
    }

    @Override
    public Promise<User> patch(UserId userId, User user) {
        return Promise.pure(userService.patch(userId, user));
    }

    @Override
    public Promise<User> get(UserId userId, @BeanParam UserGetOptions getOptions) {
        // todo:    Need to implement expand options
        return Promise.pure(userService.get(userId));
    }

    @Override
    public Promise<ResultList<User>> list(@BeanParam UserListOption listOptions) {
        // todo:    Need to implement expand and list options
        return Promise.pure(null);
    }
}
