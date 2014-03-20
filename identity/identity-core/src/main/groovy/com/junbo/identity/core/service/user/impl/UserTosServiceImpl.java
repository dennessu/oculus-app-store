/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user.impl;

import com.junbo.common.id.UserTosId;
import com.junbo.identity.core.service.user.UserTosService;
import com.junbo.identity.core.service.validator.UserTosValidator;
import com.junbo.identity.data.repository.UserTosRepository;
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
    public UserTos save(Long userId, UserTos userTosAcceptance) {
        validator.validateCreate(userId, userTosAcceptance);
        return userTosRepository.save(userTosAcceptance);
    }

    @Override
    public UserTos update(Long userId, Long userTosId, UserTos userTosAcceptance) {
        validator.validateUpdate(userId, userTosId, userTosAcceptance);
        return userTosRepository.update(userTosAcceptance);
    }

    @Override
    public UserTos get(Long userId, Long userTosId) {
        validator.validateResourceAccessible(userId, userTosId);
        return userTosRepository.get(new UserTosId(userTosId));
    }

    @Override
    public List<UserTos> getByUserId(Long userId, String tos) {
        return null;
        /*
        UserTosGetOption getOption = new UserTosGetOption();
        return userTosRepository.search(getOption);
        */
    }

    @Override
    public void delete(Long userId, Long userTosId) {
        validator.validateDelete(userId, userTosId);
        userTosRepository.delete(new UserTosId(userTosId));
    }
}
