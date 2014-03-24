/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.repository.impl
import com.junbo.common.id.GroupId
import com.junbo.identity.data.dao.GroupDAO
import com.junbo.identity.data.dao.index.GroupReverseIndexDAO
import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.identity.data.entity.reverselookup.GroupReverseIndexEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.spec.model.users.Group
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Created by liangfu on 3/14/14.
 */
class GroupRepositoryImpl implements GroupRepository {
    @Autowired
    @Qualifier('groupDAO')
    private GroupDAO groupDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('groupReverseIndexDAO')
    private GroupReverseIndexDAO groupReverseIndexDAO

    @Override
    Group get(GroupId groupId) {
        GroupEntity entity = groupDAO.get(groupId.value)
        return modelMapper.toGroup(entity, new MappingContext())
    }

    @Override
    Group save(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        groupDAO.save(groupEntity)

        GroupReverseIndexEntity entity = new GroupReverseIndexEntity()
        entity.setGroupId(groupEntity.id)
        entity.setName(groupEntity.name)
        groupReverseIndexDAO.save(entity)

        return get(new GroupId(groupEntity.id))
    }

    @Override
    Group update(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        GroupEntity existingGroupEntity = groupDAO.get(group.id.value)

        if (existingGroupEntity.name != groupEntity.name) {
            groupReverseIndexDAO.delete(existingGroupEntity.name)

            GroupReverseIndexEntity entity = new GroupReverseIndexEntity()
            entity.setName(groupEntity.name)
            entity.setGroupId(groupEntity.id)
            groupReverseIndexDAO.save(entity)
        }

        groupDAO.update(groupEntity)
        return get(group.id)
    }

    @Override
    List<Group> searchByName(String name) {
        def result = []
        def groupReverseIndexEntity = groupReverseIndexDAO.get(name)

        result.add(get(new GroupId(groupReverseIndexEntity.groupId)))
        return result
    }
}