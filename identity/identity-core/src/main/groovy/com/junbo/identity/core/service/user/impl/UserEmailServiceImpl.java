/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserEmailId;
import com.junbo.common.id.UserId;
import com.junbo.identity.core.service.user.UserEmailService;
import com.junbo.identity.core.service.validator.UserEmailValidator;
import com.junbo.identity.data.repository.UserEmailRepository;
import com.junbo.identity.spec.model.users.UserEmail;
import com.junbo.identity.spec.options.list.UserEmailListOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
@Component
@Transactional
public class UserEmailServiceImpl implements UserEmailService {

    @Autowired
    private UserEmailRepository userEmailRepository;

    @Autowired
    private UserEmailValidator validator;

    @Override
    public UserEmail save(UserId userId, UserEmail userEmail) {
        validator.validateCreate(userId, userEmail);
        userEmail.setUserId(userId);
        return userEmailRepository.save(userEmail);
    }

    @Override
    public UserEmail update(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        validator.validateUpdate(userId, userEmailId, userEmail);
        userEmail.setUserId(userId);
        return userEmailRepository.save(userEmail);
    }

    @Override
    public UserEmail patch(UserId userId, UserEmailId userEmailId, UserEmail userAuthenticator) {
        return null;
    }

    @Override
    public UserEmail get(UserId userId, UserEmailId userEmailId) {
        validator.validateResourceAccessible(userId, userEmailId);
        return userEmailRepository.get(userEmailId);
    }

    @Override
    public List<UserEmail> search(UserEmailListOptions getOption) {
        return userEmailRepository.search(getOption);
    }

    @Override
    public void delete(UserId userId, UserEmailId userEmailId) {

    }
}
