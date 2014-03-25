/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserSecurityQuestionValidator {
    void validateCreate(UserId userId, UserSecurityQuestion userSecurityQuestion);
    void validateUpdate(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                        UserSecurityQuestion userSecurityQuestion);
    void validateDelete(UserId userId, UserSecurityQuestionId userSecurityQuestionId);
    void validateResourceAccessible(UserId userId, UserSecurityQuestionId userSecurityQuestionId);
}
