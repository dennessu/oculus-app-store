/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserGroupId
import com.junbo.identity.data.dao.UserGroupDAO
import com.junbo.identity.data.entity.user.UserGroupEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserGroupRepositorySqlImpl implements UserGroupRepository {
    @Autowired
    private UserGroupDAO userGroupDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserGroup> create(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext())
        userGroupDAO.save(userGroupEntity)

        return get(new UserGroupId((Long)userGroupEntity.id))
    }

    @Override
    Promise<UserGroup> update(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext())
        userGroupDAO.update(userGroupEntity)

        return get((UserGroupId)entity.id)
    }

    @Override
    Promise<UserGroup> get(UserGroupId id) {
        return Promise.pure(modelMapper.toUserGroup(userGroupDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserGroup>> search(UserGroupListOptions getOption) {
        def entities = []
        if (getOption.userId != null) {
            entities = userGroupDAO.search(getOption.userId.value, getOption)
        } else if (getOption.groupId != null) {
            entities = userGroupDAO.findByGroupId(getOption.groupId.value, getOption)
        } else {
            throw new RuntimeException()
        }

        List<UserGroup> results = new ArrayList<UserGroup>()
        entities.each { UserGroupEntity entity ->
            results.add(modelMapper.toUserGroup(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserGroupId id) {
        userGroupDAO.delete(id.value)

        return Promise.pure(null)
    }
}
