/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.UserPasswordGetOptions;
import com.junbo.identity.spec.options.UserPasswordListOptions;
import com.junbo.identity.spec.resource.UserPasswordResource;
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
public class UserPasswordResourceImpl implements UserPasswordResource {
    @Override
    public Promise<User> create(UserId userId, UserPassword userPassword) {
        return null;
    }

    @Override
    public Promise<UserPassword> get(UserId userId, UserPasswordId userPasswordId,
                                     @BeanParam UserPasswordGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<ResultList<UserPassword>> list(UserId userId, @BeanParam UserPasswordListOptions listOptions) {
        return null;
    }
}
