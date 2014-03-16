/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption
import com.junbo.identity.spec.model.users.UserAuthenticator

/**
 * User Federation DAO is used to fetch/update/delete/get user
 * federation data(such as google account, facebook account) from the database
 */
interface UserAuthenticatorDAO {

    UserAuthenticator save(UserAuthenticator entity)

    UserAuthenticator update(UserAuthenticator entity)

    UserAuthenticator get(UserAuthenticatorId id)

    List<UserAuthenticator> search(UserAuthenticatorGetOption getOption)

    void delete(UserAuthenticatorId id)
}
