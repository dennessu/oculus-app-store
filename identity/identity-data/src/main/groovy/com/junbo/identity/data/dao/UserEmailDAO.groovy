/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao
import com.junbo.identity.data.entity.user.UserEmailEntity
import com.junbo.identity.spec.options.list.UserEmailListOptions
import com.junbo.sharding.annotations.SeedParam
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
interface UserEmailDAO {
    UserEmailEntity save(UserEmailEntity entity)

    UserEmailEntity update(UserEmailEntity entity)

    UserEmailEntity get(@SeedParam Long id)

    void delete(@SeedParam Long id)

    List<UserEmailEntity> search(@SeedParam Long userId, UserEmailListOptions getOption)
}
