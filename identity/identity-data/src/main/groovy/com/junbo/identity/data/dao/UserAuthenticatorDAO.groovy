/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.spec.options.list.UserAuthenticatorListOption
import com.junbo.sharding.annotations.SeedParam

/**
 * User Federation DAO is used to fetch/update/delete/get user
 * federation data(such as google account, facebook account) from the database
 */
interface UserAuthenticatorDAO {

    UserAuthenticatorEntity save(UserAuthenticatorEntity entity)

    UserAuthenticatorEntity update(UserAuthenticatorEntity entity)

    UserAuthenticatorEntity get(@SeedParam Long id)

    // todo:    This need to be done by reverse index
    List<UserAuthenticatorEntity> search(@SeedParam Long userId, UserAuthenticatorListOption getOption)

    void delete(@SeedParam Long id)
}
