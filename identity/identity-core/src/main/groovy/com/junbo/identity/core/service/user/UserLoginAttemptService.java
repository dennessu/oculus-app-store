/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.options.list.LoginAttemptListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/24/14.
 */
public interface UserLoginAttemptService {
    UserLoginAttempt get(UserLoginAttemptId userLoginAttemptId);
    UserLoginAttempt create(UserLoginAttempt userLoginAttempt);
    List<UserLoginAttempt> search(LoginAttemptListOptions getOptions);
}
