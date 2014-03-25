/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.identity.core.service.validator.UserSecurityQuestionAttemptValidator;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
public class UserSecurityQuestionAttemptValidatorImpl
        extends CommonValidator implements UserSecurityQuestionAttemptValidator {

    @Override
    public void validateGet(UserId userId, UserSecurityQuestionAttemptId userSecurityQuestionAttemptId) {

    }

    @Override
    public void validateCreate(UserId userId, UserSecurityQuestionAttempt userSecurityQuestionAttempt) {

    }

    @Override
    public void validateSearch(UserId userId, UserSecurityQuestionAttemptListOptions getOption) {

    }
}
