/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.data.repository.UserTosRepository;
import com.junbo.identity.core.service.user.UserTosService;
import com.junbo.identity.core.service.validator.UserTosValidator;
import com.junbo.identity.spec.options.list.UserTosListOptions;
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
    private UserTosRepository userTosRepository;

    @Autowired
    private UserTosValidator validator;

    @Override
    public UserTos save(UserId userId, UserTos userTos) {
        validator.validateCreate(userId, userTos);
        userTos.setUserId(userId);
        return userTosRepository.save(userTos);
    }

    @Override
    public UserTos update(UserId userId, UserTosId userTosId, UserTos userTos) {
        validator.validateUpdate(userId, userTosId, userTos);
        userTos.setUserId(userId);
        return userTosRepository.update(userTos);
    }

    @Override
    public UserTos get(UserId userId, UserTosId userTosId) {
        validator.validateResourceAccessible(userId, userTosId);
        return userTosRepository.get(userTosId);
    }

    @Override
    public List<UserTos> search(UserTosListOptions getOption) {
        return userTosRepository.search(getOption);
    }

    @Override
    public void delete(UserId userId, UserTosId userTosId) {
        validator.validateDelete(userId, userTosId);
        userTosRepository.delete(userTosId);
    }
}
