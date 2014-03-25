/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.core.service.user.UserPhoneNumberService;
import com.junbo.identity.core.service.validator.UserPhoneNumberValidator;
import com.junbo.identity.data.repository.UserPhoneNumberRepository;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import com.junbo.identity.spec.options.list.UserPhoneNumberListOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
@Transactional
public class UserPhoneNumberServiceImpl implements UserPhoneNumberService {

    @Autowired
    private UserPhoneNumberRepository userPhoneNumberRepository;

    @Autowired
    private UserPhoneNumberValidator validator;

    @Override
    public UserPhoneNumber save(UserId userId, UserPhoneNumber userPhoneNumber) {
        validator.validateCreate(userId, userPhoneNumber);
        userPhoneNumber.setUserId(userId);
        return userPhoneNumberRepository.save(userPhoneNumber);
    }

    @Override
    public UserPhoneNumber update(UserId userId, UserPhoneNumberId userPhoneNumberId, UserPhoneNumber userPhoneNumber) {
        validator.validateUpdate(userId, userPhoneNumberId, userPhoneNumber);
        userPhoneNumber.setUserId(userId);
        return userPhoneNumberRepository.update(userPhoneNumber);
    }

    @Override
    public UserPhoneNumber get(UserId userId, UserPhoneNumberId userPhoneNumberId) {
        validator.validateResourceAccessible(userId, userPhoneNumberId);
        return userPhoneNumberRepository.get(userPhoneNumberId);
    }

    @Override
    public List<UserPhoneNumber> search(UserPhoneNumberListOption listOption) {
        return userPhoneNumberRepository.search(listOption);
    }

    @Override
    public void delete(UserId userId, UserPhoneNumberId userPhoneNumberId) {

    }
}
