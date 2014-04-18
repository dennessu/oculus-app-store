/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
@CompileStatic
interface UserRepository extends IdentityBaseRepository<User, UserId> {
    Promise<User> getUserByCanonicalUsername(String canonicalUsername)
}
