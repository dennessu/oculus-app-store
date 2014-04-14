/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database
 */
@CompileStatic
interface UserTosRepository {

    Promise<UserTosAgreement> create(UserTosAgreement entity)

    Promise<UserTosAgreement> update(UserTosAgreement entity)

    Promise<UserTosAgreement> get(UserTosAgreementId id)

    Promise<List<UserTosAgreement>> search(UserTosAgreementListOptions getOption)

    Promise<Void> delete(UserTosAgreementId id)
}
