/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.UserDevice;

/**
 * Created by liangfu on 3/3/14.
 */
public interface UserDeviceValidator {
    void validateCreate(UserId userId, UserDevice userDevice);
    void validateUpdate(UserId userId, UserDeviceId userDeviceId, UserDevice userDevice);
    void validateDelete(UserId userId, UserDeviceId userDeviceId);
    void validateResourceAccessible(UserId userId, UserDeviceId userDeviceId);
}
