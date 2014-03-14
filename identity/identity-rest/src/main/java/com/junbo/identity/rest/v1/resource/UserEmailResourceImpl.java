/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.UserEmailResource;
import com.junbo.identity.spec.v1.model.options.UserEmailGetOption;
import com.junbo.identity.spec.v1.model.users.UserEmail;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserEmailResourceImpl implements UserEmailResource {
    @Override
    public Promise<UserEmail> create(UserId userId, UserEmail userEmail) {
        return null;
    }

    @Override
    public Promise<UserEmail> update(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        return null;
    }

    @Override
    public Promise<UserEmail> patch(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        return null;
    }

    @Override
    public Promise<UserEmail> get(UserId userId, UserEmailId userEmailId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserEmail>> list(UserId userId, @BeanParam UserEmailGetOption getOption) {
        return null;
    }

    @Override
    public Promise<UserEmail> delete(UserId userId, UserEmailId userEmailId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserEmail>> search(@BeanParam UserEmailGetOption getOption) {
        return null;
    }
}
