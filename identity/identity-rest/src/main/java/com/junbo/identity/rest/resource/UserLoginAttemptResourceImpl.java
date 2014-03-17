/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.LoginAttempt;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.options.LoginAttemptGetOptions;
import com.junbo.identity.spec.options.LoginAttemptListOptions;
import com.junbo.identity.spec.resource.UserLoginAttemptResource;
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
public class UserLoginAttemptResourceImpl implements UserLoginAttemptResource {
    @Override
    public Promise<User> create(UserId userId, LoginAttempt loginAttempt) {
        return null;
    }

    @Override
    public Promise<LoginAttempt> get(UserId userId, UserLoginAttemptId userLoginAttemptId,
                                     @BeanParam LoginAttemptGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<ResultList<LoginAttempt>> list(UserId userId, @BeanParam LoginAttemptListOptions listOptions) {
        return null;
    }
}
