/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.common.id.GroupId
import com.junbo.identity.data.dao.GroupDAO
import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.spec.model.options.GroupGetOption
import com.junbo.identity.spec.model.users.Group
import com.junbo.oom.core.MappingContext
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Created by liangfu on 3/14/14.
 */
class GroupDAOImpl implements GroupDAO {
    @Autowired
    @Qualifier('sessionFactory')
    private SessionFactory sessionFactory

    @Autowired
    private ModelMapper modelMapper

    @Override
    Group get(GroupId groupId) {
        return modelMapper.toGroup((GroupEntity)sessionFactory.currentSession.get(GroupEntity, groupId.value),
        new MappingContext())
    }

    @Override
    Group save(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())
        sessionFactory.currentSession.save(groupEntity)

        return get(group.id)
    }

    @Override
    Group update(Group group) {
        GroupEntity groupEntity = modelMapper.toGroup(group, new MappingContext())
        sessionFactory.currentSession.merge(groupEntity)
        sessionFactory.currentSession.flush()

        return get(group.id)
    }

    @Override
    List<Group> search(GroupGetOption getOption) {
        def result = []

        String query = 'select * from group_entity where value like ' +
                (getOption.value == null ? '\'%%\'' : '\'%' + getOption.value + '%\'') +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())
        List entities = sessionFactory.currentSession.createSQLQuery(query).addEntity(GroupEntity).list()

        entities.flatten { GroupEntity entity ->
            result.add(modelMapper.toGroup(entity, new MappingContext()))
        }
    }
}