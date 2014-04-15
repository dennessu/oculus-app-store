/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserOptinEntity
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 3/18/14.
 */
@CompileStatic
interface UserOptinDAO {
    UserOptinEntity save(UserOptinEntity entity)
    UserOptinEntity update(UserOptinEntity entity)
    UserOptinEntity get(Long id)
    List<UserOptinEntity> searchByUserId(Long userId)
    List<UserOptinEntity> searchByType(String type)
    void delete(Long id)
}