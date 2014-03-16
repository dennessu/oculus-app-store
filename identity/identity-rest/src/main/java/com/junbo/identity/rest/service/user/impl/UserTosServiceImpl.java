/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserTosDAO;
import com.junbo.identity.rest.service.user.UserTosService;
import com.junbo.identity.rest.service.validator.UserTosValidator;
import com.junbo.identity.spec.model.users.UserTos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserTosServiceImpl implements UserTosService {
    @Autowired
    private UserTosDAO userTosAcceptanceDAO;

    @Autowired
    private UserTosValidator validator;

    @Override
    public UserTos save(Long userId, UserTos userTosAcceptance) {
        validator.validateCreate(userId, userTosAcceptance);
        return userTosAcceptanceDAO.save(userTosAcceptance);
    }

    @Override
    public UserTos update(Long userId, Long userTosId, UserTos userTosAcceptance) {
        validator.validateUpdate(userId, userTosId, userTosAcceptance);
        return userTosAcceptanceDAO.update(userTosAcceptance);
    }

    @Override
    public UserTos get(Long userId, Long userTosId) {
        validator.validateResourceAccessible(userId, userTosId);
        return userTosAcceptanceDAO.get(userTosId);
    }

    @Override
    public List<UserTos> getByUserId(Long userId, String tos) {
        return userTosAcceptanceDAO.findByUserId(userId, tos);
    }

    @Override
    public void delete(Long userId, Long userTosId) {
        validator.validateDelete(userId, userTosId);
        userTosAcceptanceDAO.delete(userTosId);
    }
}
