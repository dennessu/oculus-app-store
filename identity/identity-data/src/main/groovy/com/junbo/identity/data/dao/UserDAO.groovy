/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserEntity
import groovy.transform.CompileStatic

/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
@CompileStatic
interface UserDAO {
    // User Model Layer
    UserEntity save(UserEntity user)

    UserEntity update(UserEntity user)

    UserEntity get(Long userId)

    void delete(Long userId)

    UserEntity getIdByCanonicalUsername(String username)
}
