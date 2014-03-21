/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;


import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.spec.options.list.LoginAttemptListOption;
import com.junbo.identity.spec.model.users.UserLoginAttempt;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserLoginAttemptRepository {
    UserLoginAttempt save(UserLoginAttempt entity);

    UserLoginAttempt update(UserLoginAttempt entity);

    UserLoginAttempt get(UserLoginAttemptId id);

    List<UserLoginAttempt> search(LoginAttemptListOption getOption);

    void delete(UserLoginAttemptId id);
}
