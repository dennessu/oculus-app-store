/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
interface UserSecurityQuestionAttemptValidator {
    Promise<UserSecurityQuestionVerifyAttempt> validateForGet(
            UserId userId, UserSecurityQuestionVerifyAttemptId attemptId)
    Promise<Void> validateForSearch(UserSecurityQuestionAttemptListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserSecurityQuestionVerifyAttempt userLoginAttempt)
}
