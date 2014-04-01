/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserLoginAttemptId
import com.junbo.identity.data.dao.UserLoginAttemptDAO
import com.junbo.identity.data.entity.user.UserLoginAttemptEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserLoginAttemptRepository
import com.junbo.identity.spec.model.users.UserLoginAttempt
import com.junbo.identity.spec.options.list.UserLoginAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by liangfu on 3/17/14.
 */
@Component
@CompileStatic
class UserLoginAttemptRepositoryImpl implements UserLoginAttemptRepository {
    @Autowired
    @Qualifier('userLoginAttemptDAO')
    private UserLoginAttemptDAO userLoginAttemptDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserLoginAttempt> create(UserLoginAttempt entity) {
        UserLoginAttemptEntity userLoginAttemptEntity = modelMapper.toUserLoginAttempt(entity, new MappingContext())
        userLoginAttemptDAO.save(userLoginAttemptEntity)

        return get(new UserLoginAttemptId(userLoginAttemptEntity.id))
    }

    @Override
    Promise<UserLoginAttempt> update(UserLoginAttempt entity) {
        UserLoginAttemptEntity userLoginAttemptEntity = modelMapper.toUserLoginAttempt(entity, new MappingContext())
        userLoginAttemptDAO.update(userLoginAttemptEntity)

        return get((UserLoginAttemptId)entity.id)
    }

    @Override
    Promise<UserLoginAttempt> get(UserLoginAttemptId id) {
        return Promise.pure(modelMapper.toUserLoginAttempt(userLoginAttemptDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserLoginAttempt>> search(UserLoginAttemptListOptions getOption) {
        List entities = userLoginAttemptDAO.search(getOption.userId.value, getOption)

        List<UserLoginAttempt> results = new ArrayList<UserLoginAttempt>()
        entities.each { UserLoginAttemptEntity entity ->
            results.add(modelMapper.toUserLoginAttempt(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserLoginAttemptId id) {
        userLoginAttemptDAO.delete(id.value)
        return Promise.pure(null)
    }
}
