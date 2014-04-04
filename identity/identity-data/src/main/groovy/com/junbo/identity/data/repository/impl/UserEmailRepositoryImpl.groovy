/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserEmailId
import com.junbo.identity.data.dao.UserEmailDAO
import com.junbo.identity.data.entity.user.UserEmailEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserEmailRepository
import com.junbo.identity.spec.model.users.UserEmail
import com.junbo.identity.spec.options.list.UserEmailListOptions
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
class UserEmailRepositoryImpl implements UserEmailRepository {
    @Autowired
    @Qualifier('userEmailDAO')
    private UserEmailDAO userEmailDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<Void> delete(UserEmailId id) {
        userEmailDAO.delete(id.value)
        Promise.pure(null)
    }

    @Override
    Promise<List<UserEmail>> search(UserEmailListOptions getOption) {
        List<UserEmail> results = new ArrayList<UserEmail>()
        if (getOption != null && getOption.userId != null) {
            List entities = userEmailDAO.search(getOption.userId.value, getOption)

            entities.each { UserEmailEntity entity ->
                results.add(modelMapper.toUserEmail(entity, new MappingContext()))
            }
        }
        else {
            results.add(searchByUserEmail(getOption.value))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<UserEmail> get(UserEmailId id) {
        return Promise.pure(modelMapper.toUserEmail(userEmailDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<UserEmail> update(UserEmail entity) {
        UserEmailEntity userEmailEntity = modelMapper.toUserEmail(entity, new MappingContext())
        userEmailDAO.update(userEmailEntity)

        return get((UserEmailId)entity.id)
    }

    @Override
    Promise<UserEmail> create(UserEmail entity) {
        UserEmailEntity userEmailEntity = modelMapper.toUserEmail(entity, new MappingContext())

        userEmailDAO.save(userEmailEntity)
        return get(new UserEmailId((Long)(userEmailEntity.id)))
    }

    private UserEmail searchByUserEmail(String value) {
        UserEmailEntity userEmailEntity = userEmailDAO.findIdByEmail(value)

        return modelMapper.toUserEmail(userEmailEntity, new MappingContext())
    }
}
