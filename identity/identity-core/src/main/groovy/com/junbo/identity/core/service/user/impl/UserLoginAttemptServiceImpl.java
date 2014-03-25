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
import com.junbo.identity.data.repository.UserLoginAttemptRepository;
import com.junbo.identity.data.repository.UserRepository;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserLoginAttempt;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.list.LoginAttemptListOption;
import com.junbo.identity.spec.options.list.UserListOptions;
import com.junbo.identity.spec.options.list.UserPasswordListOption;
import com.junbo.identity.spec.options.list.UserPinListOption;
import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        UserListOptions option = new UserListOptions();
        option.setUsername(split[0]);
        option.setLimit(1);
        List<User> users = null;
        try {
            users = userRepository.search(option).wrapped().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(userLoginAttempt.getType().equals("password")) {
            UserPasswordListOption passwordListOption = new UserPasswordListOption();
            passwordListOption.setUserId(users.get(0).getId());
            passwordListOption.setActive(true);
            passwordListOption.setLimit(1);
            UserPassword currentPassword = userPasswordService.search(passwordListOption).get(0);
            String hashPassword = UserPasswordUtil.hashPassword(split[1], currentPassword.getPasswordSalt());

            if(!hashPassword.equals(currentPassword.getPasswordHash())) {
                throw AppErrors.INSTANCE.userNamePasswordNotMatch(decode).exception();
            }
            userLoginAttempt.setSucceeded(true);
            userLoginAttempt.setUserId(users.get(0).getId());
        }
        else if(userLoginAttempt.getType().equals("pin")) {
            UserPinListOption userPinListOption = new UserPinListOption();
            userPinListOption.setUserId(users.get(0).getId());
            userPinListOption.setActive(true);
            userPinListOption.setLimit(1);
            UserPin currentPin = userPinService.search(userPinListOption).get(0);
            String hashPin = UserPasswordUtil.hashPassword(split[1], currentPin.getPinSalt());

            if(!hashPin.equals(currentPin.getPinHash())) {
                throw AppErrors.INSTANCE.userNamePasswordNotMatch(decode).exception();
            }
            userLoginAttempt.setSucceeded(true);
            userLoginAttempt.setUserId(users.get(0).getId());
        }
        return userLoginAttemptRepository.save(userLoginAttempt);
    }

    @Override
    public List<UserLoginAttempt> search(LoginAttemptListOption getOptions) {
        return userLoginAttemptRepository.search(getOptions);
    }
}
