/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.resource;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.resource.v1.resource.UserDeviceResource;
import com.junbo.identity.spec.v1.model.options.UserDeviceGetOption;
import com.junbo.identity.spec.v1.model.users.UserDevice;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserDeviceResourceImpl implements UserDeviceResource {
    @Override
    public Promise<UserDevice> create(UserId userId, UserDevice userDevice) {
        return null;
    }

    @Override
    public Promise<UserDevice> update(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        return null;
    }

    @Override
    public Promise<UserDevice> patch(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        return null;
    }

    @Override
    public Promise<UserDevice> get(UserId userId, UserDeviceId userDeviceId) {
        return null;
    }

    @Override
    public Promise<ResultList<UserDevice>> list(UserId userId, @BeanParam UserDeviceGetOption getOption) {
        return null;
    }

    @Override
    public Promise<Void> delete(UserId userId, UserDeviceId userDeviceId) {
        return null;
    }
}
