/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl
import com.junbo.common.id.UserDeviceId
import com.junbo.identity.data.dao.UserDeviceDAO
import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserDeviceRepository
import com.junbo.identity.spec.options.list.UserDeviceListOption
import com.junbo.identity.spec.model.users.UserDevice
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Implementation for UserDeviceDAO.
 */
class UserDeviceRepositoryImpl implements UserDeviceRepository {
    @Autowired
    @Qualifier('userDeviceDAO')
    private UserDeviceDAO userDeviceDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Override
    UserDevice save(UserDevice entity) {
        UserDeviceEntity userDeviceProfileEntity = modelMapper.toUserDevice(entity, new MappingContext())
        userDeviceDAO.save(userDeviceProfileEntity)

        return get(new UserDeviceId(userDeviceProfileEntity.id))
    }

    @Override
    UserDevice update(UserDevice entity) {
        UserDeviceEntity userDeviceEntity = modelMapper.toUserDevice(entity, new MappingContext())
        userDeviceDAO.update(userDeviceEntity)

        return get(entity.id)
    }

    @Override
    UserDevice get(UserDeviceId id) {
        return modelMapper.toUserDevice(userDeviceDAO.get(id.value), new MappingContext())
    }

    @Override
    List<UserDevice> search(UserDeviceListOption getOption) {
        def result = []
        def entities = userDeviceDAO.search(getOption.userId.value, getOption)

        entities.flatten { i ->
            result.add(modelMapper.toUserDevice(i, new MappingContext()))
        }
        return result
    }

    @Override
    void delete(UserDeviceId id) {
        userDeviceDAO.delete(id.value)
    }
}
