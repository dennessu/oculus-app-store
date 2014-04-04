/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserPasswordEntity
import com.junbo.identity.spec.options.list.UserPasswordListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/16/14.
 */
@CompileStatic
interface UserPasswordDAO {
    UserPasswordEntity save(UserPasswordEntity entity)

    UserPasswordEntity update(UserPasswordEntity entity)

    UserPasswordEntity get(Long id)

    List<UserPasswordEntity> search(Long userId, UserPasswordListOptions getOption)

    void delete(Long id)
}
