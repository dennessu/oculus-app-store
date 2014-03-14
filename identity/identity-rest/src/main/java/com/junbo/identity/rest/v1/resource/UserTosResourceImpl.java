/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.UserTosResource;
import com.junbo.identity.spec.v1.model.options.UserTosGetOption;
import com.junbo.identity.spec.v1.model.users.UserTos;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserTosResourceImpl implements UserTosResource {
    @Override
    public Promise<UserTos> create(UserId userId, UserTos userTos) {
        return null;
    }

    @Override
    public Promise<UserTos> update(UserId userId, UserTosId userTosId, UserTos userTos) {
        return null;
    }

    @Override
    public Promise<UserTos> patch(UserId userId, UserTosId userTosId, UserTos userTos) {
        return null;
    }

    @Override
    public Promise<UserTos> get(UserId userId, UserTosId userTosId) {
        return null;
    }

    @Override
    public Promise<Void> delete(UserId userId, UserTosId userTosId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserTos>> list(UserId userId, @BeanParam UserTosGetOption getOption) {
        return null;
    }
}
