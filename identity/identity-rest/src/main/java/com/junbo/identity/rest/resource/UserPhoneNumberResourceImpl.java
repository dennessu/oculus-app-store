/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import com.junbo.identity.spec.options.entity.UserPhoneNumberGetOptions;
import com.junbo.identity.spec.options.list.UserPhoneNumberListOption;
import com.junbo.identity.spec.resource.UserPhoneNumberResource;
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
public class UserPhoneNumberResourceImpl implements UserPhoneNumberResource {
    @Override
    public Promise<UserPhoneNumber> create(UserId userId, UserPhoneNumber userPhoneNumber) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> put(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                        UserPhoneNumber userPhoneNumber) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> patch(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                          UserPhoneNumber userPhoneNumber) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> delete(UserId userId, UserPhoneNumberId userPhoneNumberId) {
        return null;
    }

    @Override
    public Promise<UserPhoneNumber> get(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                        @BeanParam UserPhoneNumberGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<Results<UserPhoneNumber>> list(UserId userId, @BeanParam UserPhoneNumberListOption listOptions) {
        return null;
    }
}
