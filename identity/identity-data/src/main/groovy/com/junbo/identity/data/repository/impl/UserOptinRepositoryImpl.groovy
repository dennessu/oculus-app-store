/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl
import com.junbo.common.id.UserOptinId
import com.junbo.identity.data.dao.UserOptinDAO
import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserOptinRepository
import com.junbo.identity.spec.model.users.UserOptin
import com.junbo.identity.spec.options.UserOptinListOptions
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Implementation for UserOptinDAO.
 */
class UserOptinRepositoryImpl implements UserOptinRepository {
    @Autowired
    @Qualifier('userOptinDAO')
    private UserOptinDAO userOptinDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Override
    UserOptin save(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        userOptinDAO.save(userOptInEntity)

        return get(new UserOptinId(userOptInEntity.id))
    }

    @Override
    UserOptin update(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        userOptinDAO.update(userOptInEntity)

        return get(entity.id)
    }

    @Override
    UserOptin get(UserOptinId id) {
        return modelMapper.toUserOptin(userOptinDAO.get(id.value), new MappingContext())
    }

    @Override
    List<UserOptin> search(UserOptinListOptions getOption) {
        return new ArrayList<UserOptin>()
        /*
        def result = []
        def entities = userOptinDAO.search(getOption.userId.value, getOption)

        entities.flatten { i ->
            result.add(modelMapper.toUserOptin(i, new MappingContext()))
        }
        return result
        */
    }

    @Override
    void delete(UserOptinId id) {
        userOptinDAO.delete(id.value)
    }
}
