/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserNameEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/18/14.
 */
@CompileStatic
interface UserNameDAO {
    UserNameEntity get(Long id)
    UserNameEntity create(UserNameEntity entity)
    UserNameEntity update(UserNameEntity entity)
    void delete(Long id)
    UserNameEntity findByUserPiiId(Long userPiiId)
}
