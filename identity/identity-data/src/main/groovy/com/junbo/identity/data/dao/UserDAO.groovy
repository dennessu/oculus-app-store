/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserEntity
import com.junbo.identity.spec.model.options.UserGetOption
/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
interface UserDAO {
    // User Model Layer
    UserEntity save(UserEntity user)

    UserEntity update(UserEntity user)

    UserEntity get(Long userId)

    List<UserEntity> search(UserGetOption getOption)

    void delete(Long userId)
}
