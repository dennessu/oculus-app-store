/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.UserEmailId
import com.junbo.identity.spec.model.users.UserEmail
import com.junbo.identity.spec.options.list.UserEmailListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserEmailRepository {
    Promise<UserEmail> create(UserEmail entity)

    Promise<UserEmail> update(UserEmail entity)

    Promise<UserEmail> get(UserEmailId id)

    Promise<List<UserEmail>> search(UserEmailListOptions getOption)

    Promise<Void> delete(UserEmailId id)
}
