/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.core.service.validator.UserSecurityQuestionValidator;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
public class UserSecurityQuestionValidatorImpl extends CommonValidator implements UserSecurityQuestionValidator {
    @Override
    public void validateCreate(UserId userId, UserSecurityQuestion userSecurityQuestion) {

    }

    @Override
    public void validateUpdate(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                               UserSecurityQuestion userSecurityQuestion) {

    }

    @Override
    public void validateDelete(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {

    }

    @Override
    public void validateResourceAccessible(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {

    }
}
