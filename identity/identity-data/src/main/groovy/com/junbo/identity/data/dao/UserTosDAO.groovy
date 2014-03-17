/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserTosEntity
import com.junbo.identity.spec.model.options.UserTosGetOption
/**
 * User tos acceptance DAO is used to fetch/update/delete/get user tos Acceptance(eg, legal) from the database
 */
interface UserTosDAO {

    UserTosEntity save(UserTosEntity entity)

    UserTosEntity update(UserTosEntity entity)

    UserTosEntity get(Long id)

    List<UserTosEntity> search(UserTosGetOption getOption)

    void delete(Long id)
}
