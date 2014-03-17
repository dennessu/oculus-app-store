/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.repository.impl
import com.junbo.common.id.GroupId
import com.junbo.identity.data.dao.GroupDAO
import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.spec.model.options.GroupGetOption
import com.junbo.identity.spec.model.users.Group
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
/**
 * Created by liangfu on 3/14/14.
 */
class GroupRepositoryImpl implements GroupRepository {
    @Autowired
    private GroupDAO groupDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Group get(GroupId groupId) {
        GroupEntity entity = groupDAO.get(groupId.value)
        return modelMapper.toGroup(entity, new MappingContext())
    }

    @Override
    Group save(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        groupDAO.save(groupEntity)

        return get(group.id)
    }

    @Override
    Group update(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        groupDAO.update(groupEntity)

        return get(group.id)
    }

    @Override
    List<Group> search(GroupGetOption getOption) {
        def result = []
        List entities = groupDAO.search(getOption)

        entities.flatten { GroupEntity entity ->
            result.add(modelMapper.toGroup(entity, new MappingContext()))
        }
        return result
    }
}