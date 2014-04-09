/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.data.dao.UserPasswordDAO
import com.junbo.identity.data.entity.user.UserPasswordEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by liangfu on 3/16/14.
 */
@Component
@CompileStatic
class UserPasswordRepositoryImpl implements UserPasswordRepository {
    @Autowired
    @Qualifier('userPasswordDAO')
    private UserPasswordDAO userPasswordDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserPassword> create(UserPassword entity) {
        UserPasswordEntity userPasswordEntity = modelMapper.toUserPassword(entity, new MappingContext())
        userPasswordDAO.save(userPasswordEntity)

        return get(new UserPasswordId(userPasswordEntity.id))
    }

    @Override
    Promise<UserPassword> update(UserPassword entity) {
        UserPasswordEntity userPasswordEntity = modelMapper.toUserPassword(entity, new MappingContext())
        userPasswordDAO.update(userPasswordEntity)

        return get((UserPasswordId)entity.id)
    }

    @Override
    Promise<UserPassword> get(UserPasswordId id) {
        return Promise.pure(modelMapper.toUserPassword(userPasswordDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserPassword>> search(UserPasswordListOptions getOption) {
        List entities = userPasswordDAO.search(getOption.userId.value, getOption)

        List<UserPassword> results = new ArrayList<UserPassword>()
        entities.each { UserPasswordEntity existing ->
            results.add(modelMapper.toUserPassword(existing, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserPasswordId id) {
        userPasswordDAO.delete(id.value)
        return Promise.pure(null)
    }
}
