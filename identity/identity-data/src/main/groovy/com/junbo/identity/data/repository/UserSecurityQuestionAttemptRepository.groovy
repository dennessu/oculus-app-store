/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
interface UserSecurityQuestionAttemptRepository {

    Promise<UserSecurityQuestionVerifyAttempt> create(UserSecurityQuestionVerifyAttempt entity)

    Promise<UserSecurityQuestionVerifyAttempt> get(UserSecurityQuestionVerifyAttemptId id)

    Promise<List<UserSecurityQuestionVerifyAttempt>> search(UserSecurityQuestionAttemptListOptions getOption)
}
