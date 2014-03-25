/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptinId;
import com.junbo.identity.data.repository.UserOptinRepository;
import com.junbo.identity.core.service.user.UserOptinService;
import com.junbo.identity.core.service.validator.UserOptinValidator;
import com.junbo.identity.spec.options.list.UserOptinListOptions;
import com.junbo.identity.spec.model.users.UserOptin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserOptinServiceImpl implements UserOptinService {
    @Autowired
    private UserOptinRepository userOptInRepository;

    @Autowired
    private UserOptinValidator validator;

    @Override
    public UserOptin save(UserId userId, UserOptin userOptIn) {
        validator.validateCreate(userId, userOptIn);
        userOptIn.setUserId(userId);
        return userOptInRepository.save(userOptIn);
    }

    @Override
    public UserOptin update(UserId userId, UserOptinId userOptInId, UserOptin userOptIn) {
        validator.validateUpdate(userId, userOptInId, userOptIn);
        userOptIn.setUserId(userId);
        return userOptInRepository.update(userOptIn);
    }

    @Override
    public UserOptin get(UserId userId, UserOptinId userOptInId) {
        validator.validateResourceAccessible(userId, userOptInId);
        return userOptInRepository.get(userOptInId);
    }

    @Override
    public List<UserOptin> search(UserOptinListOptions listOption) {
        return userOptInRepository.search(listOption);
    }

    @Override
    public void delete(UserId userId, UserOptinId userOptInId) {
        validator.validateDelete(userId, userOptInId);
        userOptInRepository.delete(userOptInId);
    }
}
