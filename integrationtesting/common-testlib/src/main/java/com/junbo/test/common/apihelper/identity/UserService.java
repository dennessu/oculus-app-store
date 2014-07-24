/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.User;

import java.util.List;

/**
 * @author Jason
 *         time 3/11/2014
 *         Interface for User related API, including get/post/put user, update password and so on.
 */
public interface UserService {

    //Post a new user
    String PostUser() throws Exception;

    String PostUser(User user) throws Exception;

    String PostUser(User user, int expectedResponseCode) throws Exception;

    String PostUser(String userName, String emailAddress) throws Exception;

    String PostUser(String userName, String pwd, String emailAddress) throws Exception;

    String PostUser(String vat, Address address) throws Exception;

    String PostUser(String vat) throws Exception;

    //Get the user by userId
    String GetUserByUserId(String userId) throws Exception;

    String GetUserByUserId(String userId, int expectedResponseCode) throws Exception;

    //Get the user by userName
    List<String> GetUserByUserName(String userName) throws Exception;

    List<String> GetUserByUserName(String userName, int expectedResponseCode) throws Exception;

    //update a user
    String PutUser(String userId, User user) throws Exception;

    String PutUser(String userId, User user, int expectedResponseCode) throws Exception;

    String PostEmailVerification(String userId, String country, String locale) throws Exception;

    String PostEmailVerification(String userId, String country, String locale, int expectedResponseCode) throws Exception;


}
