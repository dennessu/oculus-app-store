/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserCredentialVerifyAttemptRepository
        extends BaseRepository<UserCredentialVerifyAttempt, UserCredentialVerifyAttemptId> {
    @ReadMethod
    Promise<List<UserCredentialVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserCredentialVerifyAttempt>> searchByUserIdAndCredentialTypeAndInterval(UserId userId, String type, Long fromTimeStamp,
                                                                               Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserCredentialVerifyAttempt>> searchNonLockPeriodHistory(UserId userId, String type, Long fromTimeStamp, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserCredentialVerifyAttempt>> searchByIPAddressAndCredentialTypeAndInterval(String ipAddress, String type, Long fromTimeStamp,
                                                                                  Integer limit, Integer offset)
}
