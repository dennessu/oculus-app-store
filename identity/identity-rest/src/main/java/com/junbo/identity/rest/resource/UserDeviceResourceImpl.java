/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserDevice;
import com.junbo.identity.spec.options.entity.UserDeviceGetOptions;
import com.junbo.identity.spec.options.list.UserDeviceListOption;
import com.junbo.identity.spec.resource.UserDeviceResource;
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
public class UserDeviceResourceImpl implements UserDeviceResource {
    @Override
    public Promise<UserDevice> create(UserId userId, UserDevice userDevice) {
        return null;
    }

    @Override
    public Promise<UserDevice> put(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        return null;
    }

    @Override
    public Promise<UserDevice> patch(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        return null;
    }

    @Override
    public Promise<Void> delete(UserId userId, UserDeviceId userDeviceId) {
        return null;
    }

    @Override
    public Promise<UserDevice> get(UserId userId, UserDeviceId userDeviceId,
                                   @BeanParam UserDeviceGetOptions getOptions) {
        return null;
    }

    @Override
    public Promise<Results<UserDevice>> list(UserId userId, @BeanParam UserDeviceListOption listOptions) {
        return null;
    }
}
