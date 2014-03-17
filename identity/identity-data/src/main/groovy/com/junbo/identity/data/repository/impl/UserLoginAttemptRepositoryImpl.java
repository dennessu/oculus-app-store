/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.data.dao.UserLoginAttemptDAO;
import com.junbo.identity.data.entity.user.UserLoginAttemptEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserLoginAttemptRepository;
import com.junbo.identity.spec.model.options.UserLoginAttemptGetOption;
import com.junbo.identity.spec.model.users.LoginAttempt;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserLoginAttemptRepositoryImpl implements UserLoginAttemptRepository {
    @Autowired
    @Qualifier("userLoginAttemptDAO")
    private UserLoginAttemptDAO userLoginAttemptDAO;

    @Autowired
    @Qualifier("modelMapperImpl")
    private ModelMapper modelMapper;

    @Override
    public LoginAttempt save(LoginAttempt entity) {
        UserLoginAttemptEntity userLoginAttemptEntity = modelMapper.toUserLoginAttempt(entity, new MappingContext());
        userLoginAttemptDAO.save(userLoginAttemptEntity);

        return get(entity.getId());
    }

    @Override
    public LoginAttempt update(LoginAttempt entity) {
        UserLoginAttemptEntity userLoginAttemptEntity = modelMapper.toUserLoginAttempt(entity, new MappingContext());
        userLoginAttemptDAO.update(userLoginAttemptEntity);

        return get(entity.getId());
    }

    @Override
    public LoginAttempt get(UserLoginAttemptId id) {
        return modelMapper.toUserLoginAttempt(userLoginAttemptDAO.get(id.getValue()), new MappingContext());
    }

    @Override
    public List<LoginAttempt> search(UserLoginAttemptGetOption getOption) {
        List entities = userLoginAttemptDAO.search(getOption);

        List<LoginAttempt> results = new ArrayList<LoginAttempt>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserLoginAttempt((UserLoginAttemptEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserLoginAttemptId id) {
        userLoginAttemptDAO.delete(id.getValue());
    }
}
