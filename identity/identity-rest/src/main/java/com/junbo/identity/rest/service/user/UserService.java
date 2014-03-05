/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user;


import com.junbo.identity.spec.model.user.User;

import java.util.List;

/**
 * Implementation for UserService.
 */
public interface UserService {
    /*
     * Save the user to database; will do the following check
     * 1):  check the password is valid;
     * 2):  check the input password strength;
     * 3):  check the user has no record before;
     * 4):  check the input user status; If non-Deleted, it will throw exception; else, create new user
     * @param user
     * @return saved user
     */
    User save(User user);

    /*
     * retrieve the user
     * @param id
     * @return
     */
    User get(Long id);

    /*
     * soft delete the user. Only mark the user status as deleted.
     * @param id
     * @return
     */
    void delete(Long id);

    /*
     * Update current user. User can only update status.
     * @param userId
     * @param user
     * @return
     */
    User update(Long userId, User user);

    /*
     * authenticate whether the userName & password match
     * @param user
     * @param password
     */
    User authenticate(String userName, String password);

    /*
     * save password
     * @param user
     * @param password
     */
    void savePassword(User user, String password);

    /*
     * Search user with userNamePrefix
     * @param userNamePrefix
     * @param cursor
     * @param count
     * @return
     */
    List<User> searchUser(String userNamePrefix, String status, Integer cursor, Integer count);

    /*
     * get user with name and status
     * @param userName
     * @param status
     * @return
     */
    List<User> getByUserName(String userName, String status);
}
