/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.identity.data.entity.user.UserAddressEntity
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * User Federation DAO is used to fetch/update/delete/get user
 * federation data(such as google account, facebook account) from the database
 */
@CompileStatic
interface UserAuthenticatorRepository extends BaseRepository<UserAuthenticator, UserAuthenticatorId> {
    @ReadMethod
    Promise<List<UserAuthenticator>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserAuthenticator>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserAuthenticator>> searchByExternalId(String externalId, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserAuthenticator>> searchByUserIdAndTypeAndExternalId(UserId userId, String type, String externalId,
                                                                        Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserAuthenticator>> searchByUserIdAndExternalId(UserId userId, String externalId, Integer limit,
                                                                 Integer offset)

    @ReadMethod
    Promise<List<UserAuthenticator>> searchByExternalIdAndType(String externalId, String type, Integer limit,
                                                               Integer offset)
}
