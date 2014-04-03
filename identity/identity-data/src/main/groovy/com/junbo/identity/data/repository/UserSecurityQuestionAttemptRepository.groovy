/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
interface UserSecurityQuestionAttemptRepository {

    Promise<UserSecurityQuestionAttempt> create(UserSecurityQuestionAttempt entity)

    Promise<UserSecurityQuestionAttempt> update(UserSecurityQuestionAttempt entity)

    Promise<UserSecurityQuestionAttempt> get(UserSecurityQuestionVerifyAttemptId id)

    Promise<List<UserSecurityQuestionAttempt>> search(UserSecurityQuestionAttemptListOptions getOption)
}
