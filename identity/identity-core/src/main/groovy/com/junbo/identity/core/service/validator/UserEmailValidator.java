/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.UserEmail;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserEmailValidator {
    void validateCreate(UserId userId, UserEmail userEmail);
    void validateUpdate(UserId userId, UserEmailId userEmailId, UserEmail userEmail);
    void validateDelete(UserId userId, UserEmailId userEmailId);
    void validateResourceAccessible(UserId userId, UserEmailId userEmailId);
}
