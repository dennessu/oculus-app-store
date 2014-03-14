/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.UserPhoneNumberResource;
import com.junbo.identity.spec.v1.model.options.UserPhoneNumberGetOption;
import com.junbo.identity.spec.v1.model.users.UserPhoneNumber;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserPhoneNumberResourceImpl implements UserPhoneNumberResource {
    @Override
    public Promise<UserPhoneNumber> create(UserId userId, UserPhoneNumber userPhoneNumber) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> update(UserId userId, UserPhoneNumberId userPhoneNumberId, UserPhoneNumber userPhoneNumber) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> patch(UserId userId, UserPhoneNumberId userPhoneNumberId, UserPhoneNumber userPhoneNumber) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> get(UserId userId, UserPhoneNumberId userPhoneNumberId) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> delete(UserId userId, UserPhoneNumberId userPhoneNumberId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserPhoneNumber>> list(UserId userId, @BeanParam UserPhoneNumberGetOption getOption) {
        return null;
    }
}
