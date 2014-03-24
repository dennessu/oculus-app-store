/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.options.list.UserListOption;

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
    User get(UserId id);

    /*
     * soft delete the user. Only mark the user status as deleted.
     * @param id
     * @return
     */
    void delete(UserId id);

    /*
     * Update current user. User can only update status.
     * @param userId
     * @param user
     * @return
     */
    User update(UserId userId, User user);

    User patch(UserId userId, User user);

    /*
     * get user with name and status
     * @param userName
     * @param status
     * @return
     */
    List<User> search(UserListOption getOptions);
}
