/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.core.service.user.UserPinService;
import com.junbo.identity.core.service.util.UserPasswordUtil;
import com.junbo.identity.core.service.validator.UserPinValidator;
import com.junbo.identity.data.repository.UserPinRepository;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.list.UserPinListOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by liangfu on 3/24/14.
 */
@Component
@Transactional
public class UserPinServiceImpl implements UserPinService {
    @Autowired
    private UserPinValidator validator;

    @Autowired
    private UserPinRepository userPinRepository;

    @Override
    public UserPin get(UserId userId, UserPinId userPinId) {
        validator.validateGet(userId, userPinId);
        return userPinRepository.get(userPinId);
    }

    @Override
    public UserPin create(UserId userId, UserPin userPin) {
        validator.validateCreate(userId, userPin);
        userPin.setUserId(userId);
        userPin.setPinSalt(UUID.randomUUID().toString());
        //todo: Need to refactor UserPasswordUtil
        userPin.setPinHash(UserPasswordUtil.hashPassword(userPin.getValue(), userPin.getPinSalt()));
        return userPinRepository.save(userPin);
    }

    @Override
    public List<UserPin> search(UserPinListOptions getOptions) {
        return userPinRepository.search(getOptions);
    }
}
