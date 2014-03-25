/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserSecurityQuestionAttemptService {
    UserSecurityQuestionAttempt get(UserId userId, UserSecurityQuestionAttemptId userSecurityQuestionAttemptId);
    UserSecurityQuestionAttempt create(UserId userId, UserSecurityQuestionAttempt userSecurityQuestionAttempt);
    List<UserSecurityQuestionAttempt> search(UserSecurityQuestionAttemptListOptions getOptions);
}
