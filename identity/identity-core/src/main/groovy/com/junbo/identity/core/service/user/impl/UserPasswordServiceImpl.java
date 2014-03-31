/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.core.service.user.UserPasswordService;
import com.junbo.identity.core.service.util.UserPasswordUtil;
import com.junbo.identity.core.service.validator.UserPasswordValidator;
import com.junbo.identity.data.repository.UserPasswordRepository;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.list.UserPasswordListOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by liangfu on 3/24/14.
 */
@Component
@Transactional
public class UserPasswordServiceImpl implements UserPasswordService {
    @Autowired
    private UserPasswordRepository userPasswordRepository;

    @Autowired
    private UserPasswordValidator userPasswordValidator;

    @Override
    public UserPassword get(UserId userId, UserPasswordId userPasswordId) {
        userPasswordValidator.validateGet(userId, userPasswordId);
        try {
            return userPasswordRepository.get(userPasswordId).wrapped().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public UserPassword create(UserId userId, UserPassword userPassword) {
        userPasswordValidator.validateCreate(userId, userPassword);
        userPassword.setUserId(userId);
        userPassword.setPasswordSalt(UUID.randomUUID().toString());
        userPassword.setStrength(UserPasswordUtil.calcPwdStrength(userPassword.getValue()));
        userPassword.setPasswordHash(
                UserPasswordUtil.hashPassword(userPassword.getValue(), userPassword.getPasswordSalt()));
        try {
            return userPasswordRepository.save(userPassword).wrapped().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<UserPassword> search(UserPasswordListOptions getOptions) {
        try {
            return userPasswordRepository.search(getOptions).wrapped().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
