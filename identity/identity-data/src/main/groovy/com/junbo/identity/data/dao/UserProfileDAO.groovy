/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.spec.model.user.UserProfile
/**
 * User DAO is used to fetch/update/delete/get user profile data from the database
 */
interface UserProfileDAO {

    UserProfile save(UserProfile entity)

    UserProfile update(UserProfile entity)

    UserProfile get(Long id)

    void delete(Long id)

    List<UserProfile> findByUser(Long userId, String type)
}

