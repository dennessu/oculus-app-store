/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserSecurityQuestionService {
    UserSecurityQuestion save(UserId userId, UserSecurityQuestion userSecurityQuestion);
    UserSecurityQuestion get(UserId userId, UserSecurityQuestionId userSecurityQuestionId);
    List<UserSecurityQuestion> search(UserSecurityQuestionListOptions listOption);
}
