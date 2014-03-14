/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptInId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.UserOptInResource;
import com.junbo.identity.spec.v1.model.options.UserOptInGetOption;
import com.junbo.identity.spec.v1.model.users.UserOptin;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserOptInResourceImpl implements UserOptInResource {
    @Override
    public Promise<UserOptin> create(UserId userId, UserOptin userOptIn) {
        return null;
    }

    @Override
    public Promise<UserOptin> update(UserId userId, UserOptInId userOptInId, UserOptin userOptIn) {
        return null;
    }

    @Override
    public Promise<UserOptin> patch(UserId userId, UserOptInId userOptInId, UserOptin userOptIn) {
        return null;
    }

    @Override
    public Promise<UserOptin> get(UserId userId, UserOptInId userOptInId) {
        return null;
    }

    @Override
    public Promise<UserOptin> delete(UserId userId, UserOptInId userOptInId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserOptin>> list(UserId userId, @BeanParam UserOptInGetOption getOption) {
        return null;
    }
}
