/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserCredentialVerifyAttemptRepository {
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt entity)

    Promise<UserCredentialVerifyAttempt> update(UserCredentialVerifyAttempt entity)

    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id)

    Promise<List<UserCredentialVerifyAttempt>> search(UserCredentialAttemptListOptions getOption)

    Promise<Void> delete(UserCredentialVerifyAttemptId id)
}
