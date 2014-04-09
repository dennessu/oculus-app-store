/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl
import com.junbo.common.id.UserPinId
import com.junbo.identity.data.dao.UserPinDAO
import com.junbo.identity.data.entity.user.UserPinEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
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
class UserPinRepositoryImpl implements UserPinRepository {
    @Autowired
    @Qualifier('userPinDAO')
    private UserPinDAO userPinDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserPin> create(UserPin entity) {
        UserPinEntity userPinEntity = modelMapper.toUserPin(entity, new MappingContext())
        userPinDAO.save(userPinEntity)

        return get(new UserPinId(userPinEntity.id))
    }

    @Override
    Promise<UserPin> update(UserPin entity) {
        UserPinEntity userPINEntity = modelMapper.toUserPin(entity, new MappingContext())
        userPinDAO.update(userPINEntity)

        return get((UserPinId)entity.id)
    }

    @Override
    Promise<UserPin> get(UserPinId id) {
        return Promise.pure(modelMapper.toUserPin(userPinDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserPin>> search(UserPinListOptions getOption) {
        List entities = userPinDAO.search(getOption.userId.value, getOption)

        List<UserPin> results = new ArrayList<UserPin>()
        entities.each { UserPinEntity entity ->
            results.add(modelMapper.toUserPin(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserPinId id) {
        userPinDAO.delete(id.value)
        return Promise.pure(null)
    }
}
