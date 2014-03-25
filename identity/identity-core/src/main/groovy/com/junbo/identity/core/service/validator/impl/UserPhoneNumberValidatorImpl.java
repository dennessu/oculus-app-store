/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.core.service.validator.UserPhoneNumberValidator;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
public class UserPhoneNumberValidatorImpl extends CommonValidator implements UserPhoneNumberValidator {

    @Override
    public void validateCreate(UserId userId, UserPhoneNumber userPhoneNumber) {

    }

    @Override
    public void validateUpdate(UserId userId, UserPhoneNumberId userPhoneNumberId, UserPhoneNumber userPhoneNumber) {

    }

    @Override
    public void validateDelete(UserId userId, UserPhoneNumberId userPhoneNumberId) {

    }

    @Override
    public void validateResourceAccessible(UserId userId, UserPhoneNumberId userPhoneNumberId) {

    }
}
