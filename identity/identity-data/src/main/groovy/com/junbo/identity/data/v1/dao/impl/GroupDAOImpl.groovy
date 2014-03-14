/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.v1.dao.impl
import com.junbo.common.id.GroupId
import com.junbo.identity.data.v1.dao.GroupDAO
import com.junbo.identity.data.v1.entity.group.GroupEntity
import com.junbo.identity.data.v1.mapper.ModelMapper
import com.junbo.identity.spec.v1.model.options.GroupGetOption
import com.junbo.identity.spec.v1.model.users.Group
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
        return modelMapper.toGroup((GroupEntity)sessionFactory.currentSession.get(GroupEntity, groupId.value))
    }

    @Override
    Group save(Group group) {
        GroupEntity groupEntity = modelMapper.toGroupEntity(group, new MappingContext())
        sessionFactory.currentSession.save(groupEntity)

        return get(group.id)
    }

    @Override
    Group update(Group group) {
        sessionFactory.currentSession.update(group)

        return get(group.id)
    }

    @Override
    List<Group> search(GroupGetOption getOption) {
        def result = []

        String query = 'select * from group where value like ' +
                (getOption.value == null ? '%%' : '%' + getOption.value + '%')
            +  ' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())
            + ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())
        List entities = sessionFactory.currentSession.createSQLQuery(query).addEntity(GroupEntity).list();

        entities.flatten {GroupEntity entity ->
            result.add(modelMapper.toGroup(entity))
        }
    }
}