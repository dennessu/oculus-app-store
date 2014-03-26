/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.repository.impl
import com.junbo.common.id.GroupId
import com.junbo.identity.data.dao.GroupDAO
import com.junbo.identity.data.dao.GroupUserDAO
import com.junbo.identity.data.dao.index.GroupReverseIndexDAO
import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.identity.data.entity.group.GroupUserEntity
import com.junbo.identity.data.entity.reverselookup.GroupReverseIndexEntity
import com.junbo.identity.data.entity.user.UserGroupEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.spec.model.users.Group
import com.junbo.identity.spec.model.users.UserGroup
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by liangfu on 3/14/14.
 */
@Component
@CompileStatic
class GroupRepositoryImpl implements GroupRepository {
    @Autowired
    @Qualifier('groupDAO')
    private GroupDAO groupDAO

    @Autowired
    @Qualifier('groupUserDAO')
    private GroupUserDAO groupUserDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('groupReverseIndexDAO')
    private GroupReverseIndexDAO groupReverseIndexDAO

    @Override
    Promise<Group> get(GroupId groupId) {
        GroupEntity entity = groupDAO.get(groupId.value)
        return Promise.pure(modelMapper.toGroup(entity, new MappingContext()))
    }

    @Override
    Promise<Group> create(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        groupDAO.save(groupEntity)

        GroupReverseIndexEntity entity = new GroupReverseIndexEntity()
        entity.setGroupId(groupEntity.id)
        entity.setName(groupEntity.name)
        groupReverseIndexDAO.save(entity)

        return get(new GroupId(groupEntity.id))
    }

    @Override
    Promise<Group> update(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        GroupEntity existingGroupEntity = groupDAO.get(((GroupId)group.id).value)

        if (existingGroupEntity.name != groupEntity.name) {
            groupReverseIndexDAO.delete(existingGroupEntity.name)

            GroupReverseIndexEntity entity = new GroupReverseIndexEntity()
            entity.setName(groupEntity.name)
            entity.setGroupId(groupEntity.id)
            groupReverseIndexDAO.save(entity)
        }

        groupDAO.update(groupEntity)
        return get((GroupId)group.id)
    }

    @Override
    Promise<Group> searchByName(String name) {
        def groupReverseIndexEntity = groupReverseIndexDAO.get(name)
        if(groupReverseIndexEntity == null) {
            return Promise.pure(null)
        }
        def group = get(new GroupId(groupReverseIndexEntity.groupId))
        return group
    }

    @Override
    Promise<List<UserGroup>> searchByGroupId(GroupId groupId) {
        def list = groupUserDAO.findByGroupId(groupId.value)

        def results = []
        list.each { GroupUserEntity entity ->
            UserGroupEntity userGroup = new UserGroupEntity(
                groupId: entity.groupId,
                userId:entity.userId
            )

            results.add(modelMapper.toUserGroup(userGroup, new MappingContext()))
        }

        return Promise.pure(results)
    }
}