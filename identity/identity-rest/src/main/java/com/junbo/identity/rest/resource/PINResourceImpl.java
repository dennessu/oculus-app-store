/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPINId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.options.UserPinGetOption;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserPIN;
import com.junbo.identity.spec.resource.PINResource;
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
public class PINResourceImpl implements PINResource {
    @Override
    public Promise<UserPIN> get(UserId userId, UserPINId userPinId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserPIN>> list(UserId userId, @BeanParam UserPinGetOption userPinGetOption) {
        return null;
    }

    @Override
    public Promise<User> post(UserId userId, UserPIN userPin) {
        return null;
    }
}
