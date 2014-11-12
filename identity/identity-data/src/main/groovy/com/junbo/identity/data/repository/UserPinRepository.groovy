/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.common.model.Results
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/16/14.
 */

@CompileStatic
interface UserPinRepository extends BaseRepository<UserPin, UserPinId> {
    @ReadMethod
    Promise<Results<UserPin>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<UserPin>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset)
}
