/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.core.service.user.UserLoginAttemptService;
import com.junbo.identity.core.service.user.UserPasswordService;
import com.junbo.identity.core.service.user.UserPinService;
import com.junbo.identity.core.service.util.Constants;
import com.junbo.identity.core.service.util.UserPasswordUtil;
import com.junbo.identity.core.service.validator.UserLoginAttemptValidator;
import com.junbo.identity.core.service.validator.UsernameValidator;
import com.junbo.identity.data.repository.UserLoginAttemptRepository;
import com.junbo.identity.data.repository.UserRepository;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.list.LoginAttemptListOptions;
import com.junbo.identity.spec.options.list.UserPasswordListOptions;
import com.junbo.identity.spec.options.list.UserPinListOptions;
import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by liangfu on 3/24/14.
 */
@Component
@Transactional
public class UserLoginAttemptServiceImpl implements UserLoginAttemptService {

    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private UserPinService userPinService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginAttemptRepository userLoginAttemptRepository;

    @Autowired
    private UserLoginAttemptValidator validator;

    @Autowired
    private UsernameValidator usernameValidator;

    @Override
    public UserLoginAttempt get(UserLoginAttemptId userLoginAttemptId) {
        validator.validateGet(userLoginAttemptId);
        return userLoginAttemptRepository.get(userLoginAttemptId);
    }

    @Override
    public UserLoginAttempt create(UserLoginAttempt userLoginAttempt) {
        validator.validateCreate(userLoginAttempt);
        String decode = Base64.decodeAsString(userLoginAttempt.getValue());
        String[] split = decode.split(Constants.COLON);
        User user = null;
        try {
            user = userRepository.getUserByCanonicalUsername(usernameValidator.normalizeUsername(split[0])).
                    wrapped().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(userLoginAttempt.getType().equals("password")) {
            UserPasswordListOptions passwordListOption = new UserPasswordListOptions();
            passwordListOption.setUserId(user.getId());
            passwordListOption.setActive(true);
            passwordListOption.setLimit(1);
            UserPassword currentPassword = userPasswordService.search(passwordListOption).get(0);
            String hashPassword = UserPasswordUtil.hashPassword(split[1], currentPassword.getPasswordSalt());

            userLoginAttempt.setSucceeded(hashPassword.equals(currentPassword.getPasswordHash()));
            userLoginAttempt.setUserId(user.getId());
        }
        else if(userLoginAttempt.getType().equals("pin")) {
            UserPinListOptions userPinListOption = new UserPinListOptions();
            userPinListOption.setUserId(user.getId());
            userPinListOption.setActive(true);
            userPinListOption.setLimit(1);
            UserPin currentPin = userPinService.search(userPinListOption).get(0);
            String hashPin = UserPasswordUtil.hashPassword(split[1], currentPin.getPinSalt());

            userLoginAttempt.setSucceeded(hashPin.equals(currentPin.getPinHash()));
            userLoginAttempt.setUserId(user.getId());
        }
        UserLoginAttempt saved = saveUserLoginAttempt(userLoginAttempt);
        if(!saved.getSucceeded()) {
            throw AppErrors.INSTANCE.invalidPassword(saved.getType()).exception();
        }
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private UserLoginAttempt saveUserLoginAttempt(UserLoginAttempt attempt) {
        return userLoginAttemptRepository.save(attempt);
    }

    @Override
    public List<UserLoginAttempt> search(LoginAttemptListOptions getOptions) {
        return userLoginAttemptRepository.search(getOptions);
    }
}
