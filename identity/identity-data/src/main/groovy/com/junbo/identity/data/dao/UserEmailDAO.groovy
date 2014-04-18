/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserEmailEntity
import com.junbo.identity.spec.options.list.UserEmailListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserEmailDAO {
    UserEmailEntity create(UserEmailEntity entity)

    UserEmailEntity update(UserEmailEntity entity)

    UserEmailEntity get(Long id)

    void delete(Long id)

    List<UserEmailEntity> search(Long userPiiId, UserEmailListOptions getOption)

    UserEmailEntity findByEmail(String email)
}
