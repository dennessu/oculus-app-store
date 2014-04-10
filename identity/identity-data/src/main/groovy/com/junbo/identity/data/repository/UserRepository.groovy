/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
interface UserRepository {

    Promise<User> create(User user)

    Promise<User> update(User user)

    Promise<User> get(UserId userId)

    Promise<Void> delete(UserId userId)

    Promise<User> getUserByCanonicalUsername(String canonicalUsername)
}
