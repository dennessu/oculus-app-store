/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserPinEntity
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/16/14.
 */
@CompileStatic
interface UserPinDAO {

    UserPinEntity save(UserPinEntity entity)

    UserPinEntity update(UserPinEntity entity)

    UserPinEntity get(Long id)

    List<UserPinEntity> search(Long userId, UserPinListOptions getOption)

    void delete(Long id)
}
