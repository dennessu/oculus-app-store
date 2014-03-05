/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user;

import com.junbo.identity.spec.model.user.UserDeviceProfile;

import java.util.List;

/**
 * Created by liangfu on 2/20/14.
 */
public interface UserDeviceProfileService {
    UserDeviceProfile save(Long userId, UserDeviceProfile userDeviceProfile);
    UserDeviceProfile update(Long userId, Long deviceProfileId, UserDeviceProfile userDeviceProfile);
    UserDeviceProfile get(Long userId, Long deviceProfileId);
    List<UserDeviceProfile> getByUserId(Long userId, String type);
    void deleteProfile(Long userId, Long deviceProfileId);
}
