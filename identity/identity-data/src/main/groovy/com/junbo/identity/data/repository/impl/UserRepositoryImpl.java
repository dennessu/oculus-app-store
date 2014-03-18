/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.dao.UserNameDAO;
import com.junbo.identity.data.dao.UserNameReverseLookupDAO;
import com.junbo.identity.data.entity.reverselookup.UserNameReverseLookupEntity;
import com.junbo.identity.data.entity.user.UserEntity;
import com.junbo.identity.data.entity.user.UserNameEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.repository.UserRepository;
import com.junbo.identity.spec.model.options.UserGetOption;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.model.users.UserName;
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

    @Autowired
    @Qualifier("userNameReverseLookupDAO")
    private UserNameReverseLookupDAO userNameReverseLookupDAO;

    @Autowired
    @Qualifier("userNameDAO")
    private UserNameDAO userNameDAO;

    @Override
    public User save(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        userEntity = userDAO.save(userEntity);

        // save name structure
        UserNameEntity userNameEntity = modelMapper.toUserName(user.getName(), new MappingContext());
        userNameEntity.setUserId(userEntity.getId());
        userNameDAO.save(userNameEntity);

        // build reverse lookup
        UserNameReverseLookupEntity reverseLookupEntity = new UserNameReverseLookupEntity();
        reverseLookupEntity.setUserId(userEntity.getId());
        reverseLookupEntity.setUserName(userEntity.getUserName());
        userNameReverseLookupDAO.save(reverseLookupEntity);

        return get(new UserId(userEntity.getId()));
    }

    @Override
    public User update(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        UserEntity existing = userDAO.get(user.getId().getValue());
        userDAO.update(userEntity);

        UserNameEntity userNameEntity = modelMapper.toUserName(user.getName(), new MappingContext());
        UserNameEntity existingUserNameEntity = userNameDAO.findByUserId(userEntity.getId());
        userNameEntity.setId(existingUserNameEntity.getId());
        userNameEntity.setUserId(existingUserNameEntity.getUserId());
        userNameDAO.update(userNameEntity);

        if(!userEntity.getUserName().equals(existing.getUserName())) {
            userNameReverseLookupDAO.delete(existing.getUserName());

            UserNameReverseLookupEntity reverseLookupEntity = new UserNameReverseLookupEntity();
            reverseLookupEntity.setUserId(userEntity.getId());
            reverseLookupEntity.setUserName(userEntity.getUserName());
            userNameReverseLookupDAO.save(reverseLookupEntity);
        }

        return get(user.getId());
    }

    @Override
    public User get(UserId userId) {
        User user = modelMapper.toUser(userDAO.get(userId.getValue()), new MappingContext());
        UserName userName = modelMapper.toUserName(userNameDAO.findByUserId(userId.getValue()), new MappingContext());
        user.setName(userName);

        return user;
    }

    @Override
    public List<User> search(UserGetOption getOption) {
        UserNameReverseLookupEntity reverseEntity = userNameReverseLookupDAO.get(getOption.getUserName());

        List<User> results = new ArrayList<User>();
        results.add(get(new UserId(reverseEntity.getUserId())));

        return results;
    }

    @Override
    public void delete(UserId userId) {
        UserEntity userEntity = userDAO.get(userId.getValue());
        UserNameEntity userNameEntity = userNameDAO.findByUserId(userId.getValue());
        userNameDAO.delete(userNameEntity.getId());

        userNameReverseLookupDAO.delete(userEntity.getUserName());
        userDAO.delete(userId.getValue());
    }
}
