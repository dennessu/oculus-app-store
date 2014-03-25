/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.spec.options.list.UserAuthenticatorListOption
import com.junbo.identity.spec.model.users.UserAuthenticator

/**
 * User Federation DAO is used to fetch/update/delete/get user
 * federation data(such as google account, facebook account) from the database
 */
interface UserAuthenticatorRepository {

    UserAuthenticator save(UserAuthenticator entity)

    UserAuthenticator update(UserAuthenticator entity)

    UserAuthenticator get(UserAuthenticatorId id)

    List<UserAuthenticator> search(UserAuthenticatorListOption getOption)

    void delete(UserAuthenticatorId id)
}
