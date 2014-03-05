/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.identity.data.dao.UserFederationDAO;
import com.junbo.identity.rest.service.user.UserFederationService;
import com.junbo.identity.rest.service.validator.UserFederationValidator;
import com.junbo.identity.spec.model.user.UserFederation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserFederationServiceImpl implements UserFederationService {
    @Autowired
    private UserFederationDAO userFederationDAO;

    @Autowired
    private UserFederationValidator validator;

    @Override
    public UserFederation save(Long userId, UserFederation userFederation) {
        validator.validateCreate(userId, userFederation);
        return userFederationDAO.save(userFederation);
    }

    @Override
    public UserFederation update(Long userId, Long federationId, UserFederation userFederation) {
        validator.validateUpdate(userId, federationId, userFederation);
        return userFederationDAO.update(userFederation);
    }

    @Override
    public UserFederation get(Long userId, Long federationId) {
        validator.validateResourceAccessible(userId, federationId);
        return userFederationDAO.get(federationId);
    }

    @Override
    public List<UserFederation> getByUserId(Long userId, String type) {
        return userFederationDAO.findByUser(userId, type);
    }

    @Override
    public void delete(Long userId, Long federationId) {
        validator.validateDelete(userId, federationId);
        userFederationDAO.delete(federationId);
    }
}
