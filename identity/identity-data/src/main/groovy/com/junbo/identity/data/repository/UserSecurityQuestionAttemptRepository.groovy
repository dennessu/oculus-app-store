/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
interface UserSecurityQuestionAttemptRepository
        extends BaseRepository<UserSecurityQuestionVerifyAttempt, UserSecurityQuestionVerifyAttemptId> {
    @ReadMethod
    Promise<Results<UserSecurityQuestionVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<UserSecurityQuestionVerifyAttempt>> searchByUserIdAndSecurityQuestionId(UserId userId,
                                         UserSecurityQuestionId userSecurityQuestionId, Integer limit, Integer offset)
}
