/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.core.service.user.UserPasswordService;
import com.junbo.identity.core.service.user.UserService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.entity.UserPasswordGetOptions;
import com.junbo.identity.spec.options.list.UserPasswordListOption;
import com.junbo.identity.spec.resource.UserPasswordResource;
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
public class UserPasswordResourceImpl implements UserPasswordResource {
    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private UserService userService;

    @Override
    public Promise<User> create(UserId userId, UserPassword userPassword) {
        userPasswordService.create(userId, userPassword);
        return Promise.pure(userService.get(userId));
    }

    @Override
    public Promise<UserPassword> get(UserId userId, UserPasswordId userPasswordId,
                                     @BeanParam UserPasswordGetOptions getOptions) {
        // todo:    Need to expand getOptions
        return Promise.pure(userPasswordService.get(userId, userPasswordId));
    }

    @Override
    public Promise<ResultList<UserPassword>> list(UserId userId, @BeanParam UserPasswordListOption listOptions) {
        // todo:    Need to split expand and list getOptions
        return Promise.pure(null);
    }
}
