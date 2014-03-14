/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.PINResource;
import com.junbo.identity.spec.v1.model.options.UserPinGetOption;
import com.junbo.identity.spec.v1.model.users.User;
import com.junbo.identity.spec.v1.model.users.UserPin;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class PINResourceImpl implements PINResource {
    @Override
    public Promise<UserPin> get(UserId userId, UserPinId userPinId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserPin>> list(UserId userId, @BeanParam UserPinGetOption userPinGetOption) {
        return null;
    }

    @Override
    public Promise<User> post(UserId userId, UserPin userPin) {
        return null;
    }
}
