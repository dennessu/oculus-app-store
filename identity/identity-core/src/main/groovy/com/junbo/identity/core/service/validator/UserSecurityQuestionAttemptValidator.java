/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionAttemptId;
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOption;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserSecurityQuestionAttemptValidator {
    void validateGet(UserId userId, UserSecurityQuestionAttemptId userSecurityQuestionAttemptId);
    void validateCreate(UserId userId, UserSecurityQuestionAttempt userSecurityQuestionAttempt);
    void validateSearch(UserId userId, UserSecurityQuestionAttemptListOption getOption);
}
