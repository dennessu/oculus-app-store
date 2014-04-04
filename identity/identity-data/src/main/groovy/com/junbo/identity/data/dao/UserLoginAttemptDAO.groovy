/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserLoginAttemptEntity
import com.junbo.identity.spec.options.list.UserLoginAttemptListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserLoginAttemptDAO {
    UserLoginAttemptEntity save(UserLoginAttemptEntity entity)

    UserLoginAttemptEntity update(UserLoginAttemptEntity entity)

    UserLoginAttemptEntity get(Long id)

    List<UserLoginAttemptEntity> search(Long userId, UserLoginAttemptListOptions getOption)

    void delete(Long id)
}
