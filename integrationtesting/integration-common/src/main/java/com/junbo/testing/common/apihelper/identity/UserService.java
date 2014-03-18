/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.identity;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.User;

/**
 * @author Jason
 * time 3/11/2014
 * Interface for User related API, including get/post/put user, update password and so on.
 */
public interface UserService {

    //Post a new user
    User PostUser() throws Exception;

    //Get the user by userId
    User GetUserByUserId(UserId userId) throws Exception;

    //Get the user by userName
    ResultList<User> GetUserByUserName(String userName) throws Exception;

    //update a user
    User PutUser(String userName, String status) throws Exception;

    //Authenticate user
    User AuthenticateUser(String userName, String password) throws Exception;

    //update password
    User UpdatePassword(UserId userId, String oldPassword, String newPassword) throws Exception;

    //reset password
    User ResetPassword(UserId userId, String newPassword) throws Exception;
}
