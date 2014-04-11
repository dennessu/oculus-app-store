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
import com.junbo.identity.spec.v1.model.Group
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 3/14/14.
 */
@CompileStatic
class GroupRepositoryImpl implements GroupRepository {
    @Autowired
    private GroupDAO groupDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<Group> get(GroupId groupId) {
        GroupEntity entity = groupDAO.get(groupId.value)
        return Promise.pure(modelMapper.toGroup(entity, new MappingContext()))
    }

    @Override
    Promise<Group> create(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        groupDAO.save(groupEntity)

        return get(new GroupId((Long)groupEntity.id))
    }

    @Override
    Promise<Group> update(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())

        groupDAO.update(groupEntity)
        return get((GroupId)group.id)
    }

    @Override
    Promise<Group> searchByName(String name) {
        GroupEntity groupEntity = groupDAO.findIdByName(name)
        if (groupEntity == null) {
            return Promise.pure(null)
        }
        return get(new GroupId((Long)groupEntity.id))
    }
}