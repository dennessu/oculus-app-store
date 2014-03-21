/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository
import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.options.UserGetOption
import com.junbo.identity.spec.model.users.User
/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
interface UserRepository {
    // User Model Layer
    User save(User user)

    User update(User user)

    User get(UserId userId)

    List<User> search(UserGetOption getOption)

    void delete(UserId userId)
}
