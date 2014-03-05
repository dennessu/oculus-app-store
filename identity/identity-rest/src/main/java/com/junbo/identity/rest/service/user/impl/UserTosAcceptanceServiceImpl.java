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
    public UserTosAcceptance update(Long userId, Long userTosAcceptanceId, UserTosAcceptance userTosAcceptance) {
        validator.validateUpdate(userId, userTosAcceptanceId, userTosAcceptance);
        return userTosAcceptanceDAO.update(userTosAcceptance);
    }

    @Override
    public UserTosAcceptance get(Long userId, Long userTosAcceptanceId) {
        validator.validateResourceAccessible(userId, userTosAcceptanceId);
        return userTosAcceptanceDAO.get(userTosAcceptanceId);
    }

    @Override
    public List<UserTosAcceptance> getByUserId(Long userId, String tos) {
        return userTosAcceptanceDAO.findByUserId(userId, tos);
    }

    @Override
    public void delete(Long userId, Long userTosAcceptanceId) {
        validator.validateDelete(userId, userTosAcceptanceId);
        userTosAcceptanceDAO.delete(userTosAcceptanceId);
    }
}
