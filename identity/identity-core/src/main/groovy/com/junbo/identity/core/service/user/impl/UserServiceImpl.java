/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.repository.UserRepository;
import com.junbo.identity.core.service.user.UserService;
import com.junbo.identity.core.service.validator.UserServiceValidator;
import com.junbo.identity.spec.options.list.UserListOption;
import com.junbo.identity.spec.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional("transactionManager")
class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceValidator validator;

    @Override
    public User save(User user) {
        validator.validateCreate(user);
        return (userRepository.save(user));
    }

    @Override
    public User update(Long userId, User user) {
        validator.validateUpdate(userId, user);
        return userRepository.update(user);
    }

    @Override
    public User get(Long id) {
        return userRepository.get(new UserId(id));
    }

    @Override
    public void delete(Long id) {
        validator.validateDelete(id);
        userRepository.delete(new UserId(id));
    }

    @Override
    public List<User> search(String userName, String status) {
        UserListOption getOption = new UserListOption();
        return userRepository.search(getOption);
    }
}
