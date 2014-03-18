/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.UserPinGetOptions;
import com.junbo.identity.spec.options.UserPinListOptions;
import com.junbo.identity.spec.resource.UserPinResource;
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
public class UserPinResourceImpl implements UserPinResource {
    @Override
    public Promise<User> create(UserId userId, UserPin userPin) {
        return null;
    }

    @Override
    public Promise<UserPin> get(UserId userId, UserPinId userPinId, @BeanParam UserPinGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<ResultList<UserPin>> list(UserId userId, @BeanParam UserPinListOptions listOptions) {
        return null;
    }
}
