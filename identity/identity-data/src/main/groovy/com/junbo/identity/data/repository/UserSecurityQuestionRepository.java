/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.model.options.UserSecurityQuestionGetOption;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserSecurityQuestionRepository {
    UserSecurityQuestion save(UserSecurityQuestion entity);

    UserSecurityQuestion update(UserSecurityQuestion entity);

    UserSecurityQuestion get(UserSecurityQuestionId id);

    List<UserSecurityQuestion> search(UserSecurityQuestionGetOption getOption);

    void delete(UserSecurityQuestionId id);
}
