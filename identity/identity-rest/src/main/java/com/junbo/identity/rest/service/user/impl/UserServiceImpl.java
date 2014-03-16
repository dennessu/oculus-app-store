/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.rest.service.user.UserService;
import com.junbo.identity.rest.service.validator.UserServiceValidator;
import com.junbo.identity.spec.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional("transactionManager")
class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserServiceValidator validator;

    @Override
    public User save(User user) {
        validator.validateCreate(user);
        return (userDAO.saveUser(user));
    }

    @Override
    public User update(Long userId, User user) {
        validator.validateUpdate(userId, user);
        return userDAO.updateUser(user);
    }

    @Override
    public User get(Long id) {
        return userDAO.getUser(id);
    }

    @Override
    public void delete(Long id) {
        validator.validateDelete(id);
        userDAO.deleteUser(id);
    }

    @Override
    public User authenticate(String userName, String password) {
        return userDAO.authenticate(userName, password);
    }

    @Override
    public void savePassword(User user, String password) {
        userDAO.savePassword(user.getUsername(), password);
    }

    @Override
    public List<User> searchUser(String userNamePrefix, String status, Integer cursor, Integer count) {
        return userDAO.searchUsers(userNamePrefix, status, cursor, count);
    }

    @Override
    public List<User> getByUserName(String userName, String status) {
        return userDAO.findByUserName(userName, status);
    }
}
