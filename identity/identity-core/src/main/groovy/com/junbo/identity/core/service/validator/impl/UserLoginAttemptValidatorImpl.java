/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.core.service.validator.UserLoginAttemptValidator;
import com.junbo.identity.data.repository.UserLoginAttemptRepository;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.options.list.LoginAttemptListOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/24/14.
 */
@Component
public class UserLoginAttemptValidatorImpl extends CommonValidator implements UserLoginAttemptValidator {

    @Autowired
    private UserLoginAttemptRepository userLoginAttemptRepository;

    @Override
    public void validateGet(UserLoginAttemptId userLoginAttemptId) {

    }

    @Override
    public void validateCreate(UserLoginAttempt userLoginAttempt) {

    }

    @Override
    public void validateSearch(LoginAttemptListOptions getOption) {

    }
}
