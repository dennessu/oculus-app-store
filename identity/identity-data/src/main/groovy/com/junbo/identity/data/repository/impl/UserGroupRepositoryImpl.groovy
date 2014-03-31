/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserGroupId
import com.junbo.identity.data.dao.GroupUserDAO
import com.junbo.identity.data.dao.UserGroupDAO
import com.junbo.identity.data.entity.group.GroupUserEntity
import com.junbo.identity.data.entity.user.UserGroupEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.model.users.UserGroup
import com.junbo.identity.spec.options.list.UserGroupListOptions
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
class UserGroupRepositoryImpl implements UserGroupRepository {
    @Autowired
    @Qualifier('userGroupDAO')
    private UserGroupDAO userGroupDAO

    @Autowired
    @Qualifier('groupUserDAO')
    private GroupUserDAO groupUserDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Override
    Promise<UserGroup> create(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext())
        userGroupDAO.save(userGroupEntity)

        GroupUserEntity groupUserEntity = new GroupUserEntity()
        groupUserEntity.setGroupId(userGroupEntity.groupId)
        groupUserEntity.setUserId(userGroupEntity.userId)
        groupUserEntity.setCreatedBy(userGroupEntity.createdBy)
        groupUserEntity.setCreatedTime(userGroupEntity.createdTime)
        groupUserDAO.save(groupUserEntity)

        return get(new UserGroupId(userGroupEntity.id))
    }

    @Override
    Promise<UserGroup> update(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext())
        userGroupDAO.update(userGroupEntity)

        GroupUserEntity groupUserEntity = groupUserDAO.findByGroupIdAndUserId(userGroupEntity.groupId,
                userGroupEntity.userId)
        groupUserEntity.setUpdatedBy(userGroupEntity.updatedBy)
        groupUserEntity.setUpdatedTime(userGroupEntity.updatedTime)
        groupUserDAO.update(groupUserEntity)

        return get((UserGroupId)entity.id)
    }

    @Override
    Promise<UserGroup> get(UserGroupId id) {
        return Promise.pure(modelMapper.toUserGroup(userGroupDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserGroup>> search(UserGroupListOptions getOption) {
        List entities = userGroupDAO.search(getOption.userId.value, getOption)

        List<UserGroup> results = new ArrayList<UserGroup>()
        entities.each { UserGroupEntity entity ->
            results.add(modelMapper.toUserGroup(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserGroupId id) {
        UserGroupEntity entity = userGroupDAO.get(id.value)

        GroupUserEntity groupUserEntity = groupUserDAO.findByGroupIdAndUserId(entity.groupId, entity.userId)
        groupUserDAO.delete(groupUserEntity.id)
        userGroupDAO.delete(id.value)
        return Promise.pure(null)
    }
}
