/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao
import com.junbo.identity.data.entity.user.UserEntity
import com.junbo.identity.spec.model.options.UserGetOption
import com.junbo.sharding.annotations.SeedParam

/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
interface UserDAO {
    // User Model Layer
    UserEntity save(UserEntity user)

    UserEntity update(UserEntity user)

    UserEntity get(@SeedParam Long userId)

    void delete(@SeedParam Long userId)

    //Todo: Need to build reverse lookup table
    List<UserEntity> search(UserGetOption getOption)
}
