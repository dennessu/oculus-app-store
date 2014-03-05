/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource;

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
    public Promise<UserDeviceProfile> postUserDeviceProfile(Long userId, UserDeviceProfile userDeviceProfile) {
        return Promise.pure(userDeviceProfileService.save(userId, userDeviceProfile));
    }

    @Override
    public Promise<ResultList<UserDeviceProfile>> getUserDeviceProfiles(Long userId, String type,
                                                                  Integer cursor, Integer count) {
        List<UserDeviceProfile> userDeviceProfiles = userDeviceProfileService.getByUserId(userId, type);
        return Promise.pure(ResultListUtil.init(userDeviceProfiles, count));
    }

    @Override
    public Promise<UserDeviceProfile> getUserDeviceProfile(Long userId, Long deviceProfileId) {
        return Promise.pure(userDeviceProfileService.get(userId, deviceProfileId));
    }

    @Override
    public Promise<UserDeviceProfile> updateUserDeviceProfile(Long userId,
                                 Long deviceProfileId, UserDeviceProfile userDeviceProfile) {
        return Promise.pure(userDeviceProfileService.update(userId, deviceProfileId, userDeviceProfile));
    }

    @Override
    public Promise<Void> deleteUserDeviceProfile(Long userId, Long deviceProfileId) {
        userDeviceProfileService.deleteProfile(userId, deviceProfileId);

        return Promise.pure(null);
    }
}
