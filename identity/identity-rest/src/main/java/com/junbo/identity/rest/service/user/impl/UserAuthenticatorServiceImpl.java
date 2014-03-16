/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.user.impl;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.identity.data.dao.UserAuthenticatorDAO;
import com.junbo.identity.rest.service.user.UserAuthenticatorService;
import com.junbo.identity.rest.service.validator.UserAuthenticatorValidator;
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption;
import com.junbo.identity.spec.model.users.UserAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Java doc.
 */
@Component
@Transactional
public class UserAuthenticatorServiceImpl implements UserAuthenticatorService {
    @Autowired
    private UserAuthenticatorDAO userFederationDAO;

    @Autowired
    private UserAuthenticatorValidator validator;

    @Override
    public UserAuthenticator save(Long userId, UserAuthenticator userFederation) {
        validator.validateCreate(userId, userFederation);
        return userFederationDAO.save(userFederation);
    }

    @Override
    public UserAuthenticator update(Long userId, Long federationId, UserAuthenticator userFederation) {
        validator.validateUpdate(userId, federationId, userFederation);
        return userFederationDAO.update(userFederation);
    }

    @Override
    public UserAuthenticator get(Long userId, Long federationId) {
        validator.validateResourceAccessible(userId, federationId);
        return userFederationDAO.get(new UserAuthenticatorId(federationId));
    }

    @Override
    public List<UserAuthenticator> search(UserAuthenticatorGetOption getOption) {
        return userFederationDAO.search(getOption);
    }

    @Override
    public void delete(Long userId, Long federationId) {
        validator.validateDelete(userId, federationId);
        userFederationDAO.delete(new UserAuthenticatorId(federationId));
    }
}
