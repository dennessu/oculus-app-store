/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.identity.spec.model.options.UserOptinGetOption

/**
 * User OptIn DAO is used to fetch/update/delete/get user OptIn data(such as sports, news) from the database
 */
interface UserOptinDAO {

    UserOptinEntity save(UserOptinEntity entity)

    UserOptinEntity update(UserOptinEntity entity)

    UserOptinEntity get(Long id)

    List<UserOptinEntity> search(UserOptinGetOption getOption)

    void delete(Long id)
}
