/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionAttemptId
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
interface UserSecurityQuestionAttemptValidator {
    Promise<UserSecurityQuestionAttempt> validateForGet(UserId userId, UserSecurityQuestionAttemptId attemptId)
    Promise<Void> validateForSearch(UserSecurityQuestionAttemptListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserSecurityQuestionAttempt userLoginAttempt)
}
