/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.entity.user.UserEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserRepository;
import com.junbo.identity.spec.model.options.UserGetOption;
import com.junbo.identity.spec.model.users.User;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for User DAO..
 */
@Component
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("userDAO")
    private UserDAO userDAO;

    @Override
    public User save(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        userDAO.save(userEntity);

        return get(user.getId());
    }

    @Override
    public User update(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        userDAO.update(userEntity);

        return get(user.getId());
    }

    @Override
    public User get(UserId userId) {
        return modelMapper.toUser(userDAO.get(userId.getValue()), new MappingContext());
    }

    @Override
    public List<User> search(UserGetOption getOption) {
        List entities = userDAO.search(getOption);

        List<User> results = new ArrayList<User>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUser((UserEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserId userId) {
        userDAO.delete(userId.getValue());
    }
}
