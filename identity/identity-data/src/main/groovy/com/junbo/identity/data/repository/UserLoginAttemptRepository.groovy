/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.UserLoginAttemptId
import com.junbo.identity.spec.model.users.UserLoginAttempt
import com.junbo.identity.spec.options.list.UserLoginAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserLoginAttemptRepository {
    Promise<UserLoginAttempt> create(UserLoginAttempt entity)

    Promise<UserLoginAttempt> update(UserLoginAttempt entity)

    Promise<UserLoginAttempt> get(UserLoginAttemptId id)

    Promise<List<UserLoginAttempt>> search(UserLoginAttemptListOptions getOption)

    Promise<Void> delete(UserLoginAttemptId id)
}
