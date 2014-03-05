/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.spec.model.user.UserOptIn

/**
 * User OptIn DAO is used to fetch/update/delete/get user OptIn data(such as sports, news) from the database
 */
interface UserOptInDAO {

    UserOptIn save(UserOptIn entity)

    UserOptIn update(UserOptIn entity)

    UserOptIn get(Long id)

    List<UserOptIn> findByUser(Long userId, String type)

    void delete(Long id)
}
