/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.identity.spec.model.users.UserDevice;

/**
 * Created by liangfu on 3/3/14.
 */
public interface UserDeviceValidator {
    void validateCreate(Long userId, UserDevice userDeviceProfile);
    void validateUpdate(Long userId, Long deviceProfileId, UserDevice userDeviceProfile);
    void validateDelete(Long userId, Long deviceProfileId);
    void validateResourceAccessible(Long userId, Long deviceProfileId);
}
