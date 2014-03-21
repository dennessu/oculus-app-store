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
import com.junbo.identity.spec.options.list.UserOptinListOption;
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
    public UserOptin save(Long userId, UserOptin userOptIn) {
        validator.validateCreate(userId, userOptIn);
        return userOptInRepository.save(userOptIn);
    }

    @Override
    public UserOptin update(Long userId, Long userOptInId, UserOptin userOptIn) {
        validator.validateUpdate(userId, userOptInId, userOptIn);
        return userOptInRepository.update(userOptIn);
    }

    @Override
    public UserOptin get(Long userId, Long userOptInId) {
        validator.validateResourceAccessible(userId, userOptInId);
        return userOptInRepository.get(new UserOptinId(userOptInId));
    }

    @Override
    public List<UserOptin> getByUserId(Long userId, String type) {
        UserOptinListOption getOption = new UserOptinListOption();
        getOption.setUserId(new UserId(userId));
        getOption.setValue(type);
        return userOptInRepository.search(getOption);
    }

    @Override
    public void delete(Long userId, Long userOptInId) {
        validator.validateDelete(userId, userOptInId);
        userOptInRepository.delete(new UserOptinId(userOptInId));
    }
}
