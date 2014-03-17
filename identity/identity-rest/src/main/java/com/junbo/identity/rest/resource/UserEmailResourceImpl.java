/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.UserEmail;
import com.junbo.identity.spec.options.UserEmailGetOptions;
import com.junbo.identity.spec.options.UserEmailListOptions;
import com.junbo.identity.spec.resource.UserEmailResource;
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
public class UserEmailResourceImpl implements UserEmailResource {

    @Override
    public Promise<UserEmail> create(UserId userId, UserEmail userEmail) {
        return null;
    }

    @Override
    public Promise<UserEmail> put(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        return null;
    }

    @Override
    public Promise<UserEmail> patch(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        return null;
    }

    @Override
    public Promise<UserEmail> delete(UserId userId, UserEmailId userEmailId) {
        return null;
    }

    @Override
    public Promise<UserEmail> get(UserId userId, UserEmailId userEmailId, @BeanParam UserEmailGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<ResultList<UserEmail>> list(UserId userId, @BeanParam UserEmailListOptions listOptions) {
        return null;
    }

    @Override
    public Promise<ResultList<UserEmail>> list(@BeanParam UserEmailListOptions listOptions) {
        return null;
    }
}
