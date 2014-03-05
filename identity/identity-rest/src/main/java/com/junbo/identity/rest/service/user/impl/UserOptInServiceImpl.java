/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserOptInDAO;
import com.junbo.identity.rest.service.user.UserOptInService;
import com.junbo.identity.rest.service.validator.UserOptInValidator;
import com.junbo.identity.spec.model.user.UserOptIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserOptInServiceImpl implements UserOptInService {
    @Autowired
    private UserOptInDAO userOptInDAO;

    @Autowired
    private UserOptInValidator validator;

    @Override
    public UserOptIn save(Long userId, UserOptIn userOptIn) {
        validator.validateCreate(userId, userOptIn);
        return userOptInDAO.save(userOptIn);
    }

    @Override
    public UserOptIn update(Long userId, Long userOptInId, UserOptIn userOptIn) {
        validator.validateUpdate(userId, userOptInId, userOptIn);
        return userOptInDAO.update(userOptIn);
    }

    @Override
    public UserOptIn get(Long userId, Long userOptInId) {
        validator.validateResourceAccessible(userId, userOptInId);
        return userOptInDAO.get(userOptInId);
    }

    @Override
    public List<UserOptIn> getByUserId(Long userId, String type) {
        return userOptInDAO.findByUser(userId, type);
    }

    @Override
    public void delete(Long userId, Long userOptInId) {
        validator.validateDelete(userId, userOptInId);
        userOptInDAO.delete(userOptInId);
    }
}
