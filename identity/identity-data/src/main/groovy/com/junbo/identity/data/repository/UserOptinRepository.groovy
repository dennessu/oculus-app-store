/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.UserOptinId
import com.junbo.identity.spec.model.options.UserOptinGetOption
import com.junbo.identity.spec.model.users.UserOptin

/**
 * User OptIn DAO is used to fetch/update/delete/get user OptIn data(such as sports, news) from the database
 */
interface UserOptinRepository {

    UserOptin save(UserOptin entity)

    UserOptin update(UserOptin entity)

    UserOptin get(UserOptinId id)

    List<UserOptin> search(UserOptinGetOption getOption)

    void delete(UserOptinId id)
}
