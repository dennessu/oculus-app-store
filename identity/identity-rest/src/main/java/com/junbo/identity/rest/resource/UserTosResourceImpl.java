/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.options.UserTosGetOption;
import com.junbo.identity.spec.model.users.UserTos;
import com.junbo.identity.spec.resource.UserTosResource;
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
