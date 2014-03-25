/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.identity.core.service.validator.UserEmailValidator;
import com.junbo.identity.spec.model.users.UserEmail;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
public class UserEmailValidatorImpl extends CommonValidator implements UserEmailValidator {
    @Override
    public void validateCreate(UserId userId, UserEmail userEmail) {

    }

    @Override
    public void validateUpdate(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {

    }

    @Override
    public void validateDelete(UserId userId, UserEmailId userEmailId) {

    }

    @Override
    public void validateResourceAccessible(UserId userId, UserEmailId userEmailId) {

    }
}
