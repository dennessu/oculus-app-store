/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserTosAcceptanceDAO;
import com.junbo.identity.rest.service.user.UserTosAcceptanceService;
import com.junbo.identity.rest.service.validator.UserTosAcceptanceValidator;
import com.junbo.identity.spec.model.user.UserTosAcceptance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserTosAcceptanceServiceImpl implements UserTosAcceptanceService {
    @Autowired
    private UserTosAcceptanceDAO userTosAcceptanceDAO;

    @Autowired
    private UserTosAcceptanceValidator validator;

    @Override
    public UserTosAcceptance save(Long userId, UserTosAcceptance userTosAcceptance) {
        validator.validateCreate(userId, userTosAcceptance);
        return userTosAcceptanceDAO.save(userTosAcceptance);
    }

    @Override
    public UserTosAcceptance update(Long userId, Long userTosId, UserTosAcceptance userTosAcceptance) {
        validator.validateUpdate(userId, userTosId, userTosAcceptance);
        return userTosAcceptanceDAO.update(userTosAcceptance);
    }

    @Override
    public UserTosAcceptance get(Long userId, Long userTosId) {
        validator.validateResourceAccessible(userId, userTosId);
        return userTosAcceptanceDAO.get(userTosId);
    }

    @Override
    public List<UserTosAcceptance> getByUserId(Long userId, String tos) {
        return userTosAcceptanceDAO.findByUserId(userId, tos);
    }

    @Override
    public void delete(Long userId, Long userTosId) {
        validator.validateDelete(userId, userTosId);
        userTosAcceptanceDAO.delete(userTosId);
    }
}
