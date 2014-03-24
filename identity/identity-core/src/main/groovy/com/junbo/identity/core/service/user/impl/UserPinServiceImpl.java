/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.core.service.user.UserPinService;
import com.junbo.identity.core.service.validator.UserPinValidator;
import com.junbo.identity.data.repository.UserPinRepository;
import com.junbo.identity.spec.model.users.UserPin;
import com.junbo.identity.spec.options.list.UserPinListOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/24/14.
 */
@Component
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
        return userPinRepository.save(userPin);
    }

    @Override
    public List<UserPin> search(UserPinListOption getOptions) {
        return userPinRepository.search(getOptions);
    }
}
