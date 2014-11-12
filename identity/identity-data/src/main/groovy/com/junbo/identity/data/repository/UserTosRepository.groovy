/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database
 */
@CompileStatic
interface UserTosRepository extends BaseRepository<UserTosAgreement, UserTosAgreementId> {
    @ReadMethod
    Promise<Results<UserTosAgreement>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<UserTosAgreement>> searchByTosId(TosId tosId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<UserTosAgreement>> searchByUserIdAndTosId(UserId userId, TosId tosId, Integer limit, Integer offset)
}
