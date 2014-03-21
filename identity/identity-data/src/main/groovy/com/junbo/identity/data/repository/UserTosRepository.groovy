/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserTosId
import com.junbo.identity.spec.options.list.UserTosListOption
import com.junbo.identity.spec.model.users.UserTos
/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database
 */
interface UserTosRepository {

    UserTos save(UserTos entity)

    UserTos update(UserTos entity)

    UserTos get(UserTosId id)

    List<UserTos> search(UserTosListOption getOption)

    void delete(UserTosId id)
}
