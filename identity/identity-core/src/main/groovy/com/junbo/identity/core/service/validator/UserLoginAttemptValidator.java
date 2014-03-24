/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.options.list.LoginAttemptListOption;

/**
 * Created by liangfu on 3/24/14.
 */
public interface UserLoginAttemptValidator {
    void validateGet(UserLoginAttemptId userLoginAttemptId);
    void validateCreate(UserLoginAttempt userLoginAttempt);
    void validateSearch(LoginAttemptListOption getOption);
}
