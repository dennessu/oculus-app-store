/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
@CompileStatic
interface UserRepository extends BaseRepository<User, UserId> {

    @ReadMethod
    Promise<User> searchUserByMigrateId(Long migratedUserId)

    @ReadMethod
    Promise<List<User>> searchInvalidVatUser(Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<User>> searchAll(Integer limit, String cursor)
}
