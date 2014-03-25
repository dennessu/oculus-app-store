/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.core.service.user.UserDeviceService;
import com.junbo.identity.spec.model.users.UserDevice;
import com.junbo.identity.spec.options.entity.UserDeviceGetOptions;
import com.junbo.identity.spec.options.list.UserDeviceListOptions;
import com.junbo.identity.spec.resource.UserDeviceResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserDeviceResourceImpl implements UserDeviceResource {

    @Autowired
    private UserDeviceService userDeviceService;

    @Override
    public Promise<UserDevice> create(UserId userId, UserDevice userDevice) {
        return Promise.pure(userDeviceService.save(userId, userDevice));
    }

    @Override
    public Promise<UserDevice> put(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice) {
        return Promise.pure(userDeviceService.update(userId, userDeviceId, userDevice));
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
        // Need to implement expansion
        return Promise.pure(userDeviceService.get(userId, userDeviceId));
    }

    @Override
    public Promise<Results<UserDevice>> list(UserId userId, @BeanParam UserDeviceListOptions listOptions) {
        listOptions.setUserId(userId);
        List<UserDevice> userDevices = userDeviceService.search(listOptions);
        Results<UserDevice> results = new Results<>();
        results.setItems(userDevices);
        return Promise.pure(results);
    }
}
