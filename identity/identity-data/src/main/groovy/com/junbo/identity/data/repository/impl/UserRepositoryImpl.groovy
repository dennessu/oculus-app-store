/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.UserDAO
import com.junbo.identity.data.dao.UserNameDAO
import com.junbo.identity.data.dao.index.UserNameReverseIndexDAO
import com.junbo.identity.data.entity.reverselookup.UserNameReverseIndexEntity
import com.junbo.identity.data.entity.user.UserEntity
import com.junbo.identity.data.entity.user.UserNameEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserName
import com.junbo.identity.spec.options.list.UserListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Implementation for User DAO..
 */
@Component
@CompileStatic
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    @Qualifier("identityModelMapperImpl")
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("userDAO")
    private UserDAO userDAO;

    @Autowired
    @Qualifier("userNameReverseIndexDAO")
    private UserNameReverseIndexDAO userNameReverseIndexDAO;

    @Autowired
    @Qualifier("userNameDAO")
    private UserNameDAO userNameDAO;

    @Override
    public Promise<User> create(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        userEntity = userDAO.save(userEntity);

        // save name structure
        UserNameEntity userNameEntity = modelMapper.toUserName(user.getName(), new MappingContext());
        userNameEntity.setUserId(userEntity.getId());
        userNameDAO.save(userNameEntity);

        // build reverse lookup
        UserNameReverseIndexEntity reverseLookupEntity = new UserNameReverseIndexEntity();
        reverseLookupEntity.setUserId(userEntity.getId());
        reverseLookupEntity.setUsername(userEntity.getUsername());
        userNameReverseIndexDAO.save(reverseLookupEntity);

        return get(new UserId(userEntity.getId()));
    }

    @Override
    public Promise<User> update(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        UserEntity existing = userDAO.get(user.getId().getValue());
        userDAO.update(userEntity);

        UserNameEntity userNameEntity = modelMapper.toUserName(user.getName(), new MappingContext());
        UserNameEntity existingUserNameEntity = userNameDAO.findByUserId(userEntity.getId());
        userNameEntity.setId(existingUserNameEntity.getId());
        userNameEntity.setUserId(existingUserNameEntity.getUserId());
        userNameDAO.update(userNameEntity);

        if (!userEntity.getUsername().equals(existing.getUsername())) {
            userNameReverseIndexDAO.delete(existing.getUsername());

            UserNameReverseIndexEntity reverseLookupEntity = new UserNameReverseIndexEntity();
            reverseLookupEntity.setUserId(userEntity.getId());
            reverseLookupEntity.setUsername(userEntity.getUsername());
            userNameReverseIndexDAO.save(reverseLookupEntity);
        }

        return get(user.getId());
    }

    @Override
    public Promise<User> get(UserId userId) {
        User user = modelMapper.toUser(userDAO.get(userId.getValue()), new MappingContext());
        UserName userName = modelMapper.toUserName(userNameDAO.findByUserId(userId.getValue()), new MappingContext());
        user.setName(userName);

        return Promise.pure(user);
    }

    @Override
    public Promise<List<User>> search(UserListOptions option) {
        if (!StringUtils.isEmpty(option.getUsername())) {
            UserNameReverseIndexEntity reverseEntity = userNameReverseIndexDAO.get(option.getUsername());

            List<User> results = new ArrayList<User>();
            results.add(get(new UserId(reverseEntity.getUserId())).wrapped().get());

            return Promise.pure(results);
        } else {
            // todo:    Need to identify other fields
            throw new RuntimeException();
        }
    }

    @Override
    public Promise<Void> delete(UserId userId) {
        UserEntity userEntity = userDAO.get(userId.getValue());
        UserNameEntity userNameEntity = userNameDAO.findByUserId(userId.getValue());
        userNameDAO.delete(userNameEntity.getId());

        userNameReverseIndexDAO.delete(userEntity.getUsername());
        userDAO.delete(userId.getValue());

        return Promise.pure(null)
    }

    @Override
    Promise<User> getUserByCanonicalUsername(String canonicalUsername) {
        if (StringUtils.isEmpty(canonicalUsername)) {
            throw new IllegalArgumentException('canonicalUsername is empty')
        }

        UserNameReverseIndexEntity reverseEntity = userNameReverseIndexDAO.get(canonicalUsername);

        if (reverseEntity == null) {
            return Promise.pure(null);
        }

        return get(new UserId(reverseEntity.getUserId()));
    }
}
