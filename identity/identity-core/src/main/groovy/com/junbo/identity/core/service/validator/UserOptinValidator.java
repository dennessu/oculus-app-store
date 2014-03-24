/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.identity.spec.model.users.UserOptin;

/**
 * Created by liangfu on 2/28/14.
 */
public interface UserOptinValidator {
    void validateCreate(UserId userId, UserOptin userOptIn);
    void validateUpdate(UserId userId, UserOptinId userOptinId, UserOptin userOptIn);
    void validateDelete(UserId userId, UserOptinId userOptinId);
    void validateResourceAccessible(UserId userId, UserOptinId userOptinId);
}
