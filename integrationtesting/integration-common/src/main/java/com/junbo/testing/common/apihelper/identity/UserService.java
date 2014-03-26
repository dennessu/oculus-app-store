/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.identity;

import com.junbo.identity.spec.model.user.User;
import java.util.List;

/**
 * @author Jason
 * time 3/11/2014
 * Interface for User related API, including get/post/put user, update password and so on.
 */
public interface UserService {

    //Post a new user
    String PostUser() throws Exception;
    String PostUser(User user) throws Exception;
    String PostUser(User user, int expectedResponseCode) throws Exception;

    //Get the user by userId
    String GetUserByUserId(String userId) throws Exception;
    String GetUserByUserId(String userId, int expectedResponseCode) throws Exception;

    //Get the user by userName
    List<String> GetUserByUserName(String userName) throws Exception;
    List<String> GetUserByUserName(String userName, int expectedResponseCode) throws Exception;

    //update a user
    String PutUser(String userName, String status) throws Exception;
    String PutUser(String userName, String status, int expectedResponseCode) throws Exception;

    //Authenticate user
    String AuthenticateUser(String userName, String password) throws Exception;
    String AuthenticateUser(String userName, String password, int expectedResponseCode) throws Exception;

    //update password
    String UpdatePassword(String userId, String oldPassword, String newPassword) throws Exception;
    String UpdatePassword(String userId, String oldPassword, String newPassword, int expectedResponseCode)
            throws Exception;

    //reset password
    String ResetPassword(String userId, String newPassword) throws Exception;
    String ResetPassword(String userId, String newPassword, int expectedResponseCode) throws Exception;

}
