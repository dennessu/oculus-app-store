/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserOptinDAO;
import com.junbo.identity.rest.service.user.UserOptinService;
import com.junbo.identity.rest.service.validator.UserOptinValidator;
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
    private UserOptinDAO userOptInDAO;

    @Autowired
    private UserOptinValidator validator;

    @Override
    public UserOptin save(Long userId, UserOptin userOptIn) {
        validator.validateCreate(userId, userOptIn);
        return userOptInDAO.save(userOptIn);
    }

    @Override
    public UserOptin update(Long userId, Long userOptInId, UserOptin userOptIn) {
        validator.validateUpdate(userId, userOptInId, userOptIn);
        return userOptInDAO.update(userOptIn);
    }

    @Override
    public UserOptin get(Long userId, Long userOptInId) {
        validator.validateResourceAccessible(userId, userOptInId);
        return userOptInDAO.get(userOptInId);
    }

    @Override
    public List<UserOptin> getByUserId(Long userId, String type) {
        return userOptInDAO.findByUser(userId, type);
    }

    @Override
    public void delete(Long userId, Long userOptInId) {
        validator.validateDelete(userId, userOptInId);
        userOptInDAO.delete(userOptInId);
    }
}
