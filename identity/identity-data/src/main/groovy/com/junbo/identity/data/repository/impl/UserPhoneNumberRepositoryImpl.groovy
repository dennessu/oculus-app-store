/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserPhoneNumberId
import com.junbo.identity.data.dao.UserPhoneNumberDAO
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserPhoneNumberRepository
import com.junbo.identity.spec.model.users.UserPhoneNumber
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
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
class UserPhoneNumberRepositoryImpl implements UserPhoneNumberRepository {
    @Autowired
    @Qualifier('userPhoneNumberDAO')
    private UserPhoneNumberDAO userPhoneNumberDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Override
    Promise<UserPhoneNumber> create(UserPhoneNumber entity) {
        UserPhoneNumberEntity userPhoneNumberEntity = modelMapper.toUserPhoneNumber(entity, new MappingContext())
        userPhoneNumberDAO.save(userPhoneNumberEntity)

        return get(new UserPhoneNumberId(userPhoneNumberEntity.id))
    }

    @Override
    Promise<UserPhoneNumber> update(UserPhoneNumber entity) {
        UserPhoneNumberEntity userPhoneNumberEntity = modelMapper.toUserPhoneNumber(entity, new MappingContext())
        userPhoneNumberDAO.update(userPhoneNumberEntity)

        return get((UserPhoneNumberId)entity.id)
    }

    @Override
    Promise<UserPhoneNumber> get(UserPhoneNumberId id) {
        return Promise.pure(modelMapper.toUserPhoneNumber(userPhoneNumberDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserPhoneNumber>> search(UserPhoneNumberListOptions getOption) {
        List entities = userPhoneNumberDAO.search(getOption.userId.value, getOption)

        List<UserPhoneNumber> results = new ArrayList<UserPhoneNumber>()
        entities.each { UserPhoneNumberEntity entity ->
            results.add(modelMapper.toUserPhoneNumber(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserPhoneNumberId id) {
        userPhoneNumberDAO.delete(id.value)
        return Promise.pure(null)
    }
}
