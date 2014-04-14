/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserCredentialVerifyAttemptEntity
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserCredentialVerifyAttemptDAO {
    UserCredentialVerifyAttemptEntity save(UserCredentialVerifyAttemptEntity entity)

    UserCredentialVerifyAttemptEntity update(UserCredentialVerifyAttemptEntity entity)

    UserCredentialVerifyAttemptEntity get(Long id)

    List<UserCredentialVerifyAttemptEntity> search(Long userId, UserCredentialAttemptListOptions getOption)

    void delete(Long id)
}
