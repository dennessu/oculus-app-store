/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.rest.service.user.UserService;
import com.junbo.identity.rest.service.validator.UserServiceValidator;
import com.junbo.identity.spec.model.options.UserGetOption;
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
        return (userDAO.save(user));
    }

    @Override
    public User update(Long userId, User user) {
        validator.validateUpdate(userId, user);
        return userDAO.update(user);
    }

    @Override
    public User get(Long id) {
        return userDAO.get(new UserId(id));
    }

    @Override
    public void delete(Long id) {
        validator.validateDelete(id);
        userDAO.delete(new UserId(id));
    }

    @Override
    public List<User> search(String userName, String status) {
        UserGetOption getOption = new UserGetOption();
        return userDAO.search(getOption);
    }
}
