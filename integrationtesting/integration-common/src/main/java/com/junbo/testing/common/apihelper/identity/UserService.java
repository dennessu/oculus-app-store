/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.testing.common.apihelper.identity;

/**
 * @author Jason
 * time 3/11/2014
 * Interface for User related API, including get/post/put user, update password and so on.
 */
public interface UserService {

    //Post a new user
    String PostUser() throws Exception;

    //Get the user by userId
    String GetUserByUserId(String userId) throws Exception;

    //Get the user by userName
    String GetUserByUserName(String userName) throws Exception;

    //update a user
    String PutUser(String userName, String status) throws Exception;

    //Authenticate user
    String AuthenticateUser(String userName, String password) throws Exception;

    //update password
    String UpdatePassword(String userId, String oldPassword, String newPassword) throws Exception;

    //reset password
    String ResetPassword(String userId, String newPassword) throws Exception;
}
