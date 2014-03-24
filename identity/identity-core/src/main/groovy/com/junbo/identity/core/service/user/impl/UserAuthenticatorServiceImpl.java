/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.id.UserId;
import com.junbo.identity.data.repository.UserAuthenticatorRepository;
import com.junbo.identity.core.service.user.UserAuthenticatorService;
import com.junbo.identity.core.service.validator.UserAuthenticatorValidator;
import com.junbo.identity.spec.options.list.UserAuthenticatorListOption;
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
    private UserAuthenticatorRepository userFederationRepository;

    @Autowired
    private UserAuthenticatorValidator validator;

    @Override
    public UserAuthenticator save(UserId userId, UserAuthenticator userAuthenticator) {
        validator.validateCreate(userId, userAuthenticator);
        return userFederationRepository.save(userAuthenticator);
    }

    @Override
    public UserAuthenticator update(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                    UserAuthenticator userAuthenticator) {
        validator.validateUpdate(userId, userAuthenticatorId, userAuthenticator);
        return userFederationRepository.update(userAuthenticator);
    }

    @Override
    public UserAuthenticator get(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        validator.validateResourceAccessible(userId, userAuthenticatorId);
        return userFederationRepository.get(userAuthenticatorId);
    }

    @Override
    public List<UserAuthenticator> search(UserAuthenticatorListOption getOption) {
        return userFederationRepository.search(getOption);
    }

    @Override
    public void delete(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        validator.validateDelete(userId, userAuthenticatorId);
        userFederationRepository.delete(userAuthenticatorId);
    }
}
