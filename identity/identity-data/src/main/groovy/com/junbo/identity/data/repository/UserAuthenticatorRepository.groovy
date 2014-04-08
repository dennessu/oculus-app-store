/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * User Federation DAO is used to fetch/update/delete/get user
 * federation data(such as google account, facebook account) from the database
 */
@CompileStatic
interface UserAuthenticatorRepository {

    Promise<UserAuthenticator> create(UserAuthenticator entity)

    Promise<UserAuthenticator> update(UserAuthenticator entity)

    Promise<UserAuthenticator> get(UserAuthenticatorId id)

    Promise<List<UserAuthenticator>> search(AuthenticatorListOptions getOption)

    Promise<Void> delete(UserAuthenticatorId id)
}
