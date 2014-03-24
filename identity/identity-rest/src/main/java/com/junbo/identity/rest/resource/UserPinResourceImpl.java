/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.core.service.user.UserPinService;
import com.junbo.identity.core.service.user.UserService;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.entity.UserPinGetOptions;
import com.junbo.identity.spec.options.list.UserPinListOption;
import com.junbo.identity.spec.resource.UserPinResource;
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
public class UserPinResourceImpl implements UserPinResource {
    @Autowired
    private UserPinService userPinService;

    @Autowired
    private UserService userService;

    @Override
    public Promise<User> create(UserId userId, UserPin userPin) {
        userPinService.create(userId, userPin);
        return Promise.pure(userService.get(userId));
    }

    @Override
    public Promise<UserPin> get(UserId userId, UserPinId userPinId, @BeanParam UserPinGetOptions getOptions) {
        // todo:    Need to implement expand option
        return null;
    }

    @Override
    public Promise<Results<UserPin>> list(UserId userId, @BeanParam UserPinListOption listOptions) {
        // todo:    Need to implement expand and list option
        return null;
    }
}
