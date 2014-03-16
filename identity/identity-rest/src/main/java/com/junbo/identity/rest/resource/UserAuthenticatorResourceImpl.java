/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.UserAuthenticatorResource;
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption;
import com.junbo.identity.spec.model.users.UserAuthenticator;
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
public class UserAuthenticatorResourceImpl implements UserAuthenticatorResource {
    @Override
    public Promise<UserAuthenticator> create(UserId userId, UserAuthenticator userAuthenticator) {
        return null;
    }

    @Override
    public Promise<UserAuthenticator> update(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                             UserAuthenticator userAuthenticator) {
        return null;
    }

    @Override
    public Promise<UserAuthenticator> patch(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                            UserAuthenticator userAuthenticator) {
        return null;
    }

    @Override
    public Promise<UserAuthenticator> get(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserAuthenticator>> list(UserId userId, @BeanParam UserAuthenticatorGetOption getOption) {
        return null;
    }

    @Override
    public Promise<Void> delete(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserAuthenticator>> search(@BeanParam UserAuthenticatorGetOption getOption) {
        return null;
    }
}
