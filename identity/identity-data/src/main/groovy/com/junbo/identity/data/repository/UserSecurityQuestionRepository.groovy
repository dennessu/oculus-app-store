/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.spec.model.users.UserSecurityQuestion
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserSecurityQuestionRepository {
    Promise<UserSecurityQuestion> create(UserSecurityQuestion entity)

    Promise<UserSecurityQuestion> update(UserSecurityQuestion entity)

    Promise<UserSecurityQuestion> get(UserSecurityQuestionId id)

    Promise<List<UserSecurityQuestion>> search(UserSecurityQuestionListOptions getOption)

    Promise<Void> delete(UserSecurityQuestionId id)
}
