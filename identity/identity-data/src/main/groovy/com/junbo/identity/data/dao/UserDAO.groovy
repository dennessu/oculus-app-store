/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserPasswordBlacklist
import com.junbo.identity.spec.model.users.User

/**
 * User DAO is used to fetch/update/delete/get user data from the database
 */
interface UserDAO {
    // User Model Layer
    User saveUser(User user)

    User updateUser(User user)

    User getUser(Long userId)

    List<User> findByUserName(String userName, String status)

    void deleteUser(Long userId)

    List<User> searchUsers(String userNamePrefix, String status, Integer cursor, Integer count)

    // password Layer
    User authenticate(String userName, String password)

    void savePassword(String userName, String password)

    List<UserPasswordBlacklist> getPasswordBlacklists()
}
