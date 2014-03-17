/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;


import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.spec.model.options.UserLoginAttemptGetOption;
import com.junbo.identity.spec.model.users.LoginAttempt;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserLoginAttemptRepository {
    LoginAttempt save(LoginAttempt entity);

    LoginAttempt update(LoginAttempt entity);

    LoginAttempt get(UserLoginAttemptId id);

    List<LoginAttempt> search(UserLoginAttemptGetOption getOption);

    void delete(UserLoginAttemptId id);
}
