/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * User OptIn DAO is used to fetch/update/delete/get user OptIn data(such as sports, news) from the database
 */
@CompileStatic
interface UserCommunicationRepository extends BaseRepository<UserCommunication, UserCommunicationId> {
    @ReadMethod
    Promise<Results<UserCommunication>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<UserCommunication>> searchByCommunicationId(CommunicationId communicationId, Integer limit,
                                                             Integer offset)

    @ReadMethod
    Promise<Results<UserCommunication>> searchByUserIdAndCommunicationId(UserId userId, CommunicationId communicationId,
                                                                      Integer limit, Integer offset)
}
