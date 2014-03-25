/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserLoginAttemptService;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.options.entity.LoginAttemptGetOptions;
import com.junbo.identity.spec.options.list.LoginAttemptListOption;
import com.junbo.identity.spec.resource.UserLoginAttemptResource;
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
public class UserLoginAttemptResourceImpl implements UserLoginAttemptResource {

    @Autowired
    private UserLoginAttemptService userLoginAttemptService;

    @Override
    public Promise<UserLoginAttempt> create(UserLoginAttempt loginAttempt) {
        return Promise.pure(userLoginAttemptService.create(loginAttempt));
    }

    @Override
    public Promise<UserLoginAttempt> get(UserLoginAttemptId userLoginAttemptId,
                                         @BeanParam LoginAttemptGetOptions getOptions) {
        // todo:    Need to do expansion.
        return Promise.pure(userLoginAttemptService.get(userLoginAttemptId));
    }

    @Override
    public Promise<Results<UserLoginAttempt>> list(@BeanParam LoginAttemptListOption listOptions) {
        // todo:    Need to implement this.
        return null;
    }
}
