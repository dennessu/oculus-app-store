/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserGroupEntity
import com.junbo.identity.spec.options.list.UserGroupListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserGroupDAO {
    UserGroupEntity save(UserGroupEntity entity)

    UserGroupEntity update(UserGroupEntity entity)

    UserGroupEntity get(Long id)

    List<UserGroupEntity> search(Long userId, UserGroupListOptions getOption)

    void delete(Long id)
}
