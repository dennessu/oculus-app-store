/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.spec.model.users.UserPhoneNumber;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserPhoneNumberValidator {
    void validateCreate(UserId userId, UserPhoneNumber userPhoneNumber);
    void validateUpdate(UserId userId, UserPhoneNumberId userPhoneNumberId, UserPhoneNumber userPhoneNumber);
    void validateDelete(UserId userId, UserPhoneNumberId userPhoneNumberId);
    void validateResourceAccessible(UserId userId, UserPhoneNumberId userPhoneNumberId);
}
