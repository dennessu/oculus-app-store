/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.users.UserOptin;
import com.junbo.identity.spec.options.UserOptinGetOptions;
import com.junbo.identity.spec.options.UserOptinListOptions;
import com.junbo.identity.spec.resource.UserOptinResource;
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
public class UserOptinResourceImpl implements UserOptinResource {
    @Override
    public Promise<UserOptin> create(UserId userId, UserOptin userOptIn) {
        return null;
    }

    @Override
    public Promise<UserOptin> put(UserId userId, UserOptinId userOptInId, UserOptin userOptIn) {
        return null;
    }

    @Override
    public Promise<UserOptin> patch(UserId userId, UserOptinId userOptInId, UserOptin userOptIn) {
        return null;
    }

    @Override
    public Promise<UserOptin> get(UserId userId, UserOptinId userOptInId, UserOptinGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<UserOptin> delete(UserId userId, UserOptinId userOptInId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserOptin>> list(UserId userId, @BeanParam UserOptinListOptions listOptions) {
        return null;
    }
}
