/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

import com.junbo.common.id.UserDeviceProfileId;
import com.junbo.common.id.UserId;
import com.junbo.identity.rest.service.user.UserDeviceProfileService;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.common.ResultListUtil;
import com.junbo.identity.spec.model.user.UserDeviceProfile;
import com.junbo.identity.spec.resource.UserDeviceProfileResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by liangfu on 2/13/14.
 */
@Provider
@Component
@org.springframework.context.annotation.Scope("prototype")
public class UserDeviceProfileResourceImpl implements UserDeviceProfileResource{
    @Autowired
    private UserDeviceProfileService userDeviceProfileService;

    @Override
    public Promise<UserDeviceProfile> postUserDeviceProfile(UserId userId, UserDeviceProfile userDeviceProfile) {
        return Promise.pure(userDeviceProfileService.save(userId.getValue(), userDeviceProfile));
    }

    @Override
    public Promise<ResultList<UserDeviceProfile>> getUserDeviceProfiles(UserId userId, String type,
                                                                  Integer cursor, Integer count) {
        List<UserDeviceProfile> userDeviceProfiles = userDeviceProfileService.getByUserId(userId.getValue(), type);
        return Promise.pure(ResultListUtil.init(userDeviceProfiles, count));
    }

    @Override
    public Promise<UserDeviceProfile> getUserDeviceProfile(UserId userId, UserDeviceProfileId deviceProfileId) {
        return Promise.pure(userDeviceProfileService.get(userId.getValue(), deviceProfileId.getValue()));
    }

    @Override
    public Promise<UserDeviceProfile> updateUserDeviceProfile(UserId userId,
                                 UserDeviceProfileId deviceProfileId, UserDeviceProfile userDeviceProfile) {
        return Promise.pure(userDeviceProfileService.update(userId.getValue(),
                deviceProfileId.getValue(), userDeviceProfile));
    }

    @Override
    public Promise<Void> deleteUserDeviceProfile(UserId userId, UserDeviceProfileId deviceProfileId) {
        userDeviceProfileService.deleteProfile(userId.getValue(), deviceProfileId.getValue());

        return Promise.pure(null);
    }
}
