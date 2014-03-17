/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.GroupDAO
import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.identity.spec.model.options.GroupGetOption
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

    @Override
    GroupEntity get(Long groupId) {
        return (GroupEntity)sessionFactory.currentSession.get(GroupEntity, groupId)
    }

    @Override
    GroupEntity save(GroupEntity group) {
        sessionFactory.currentSession.save(group)

        return get(group.id)
    }

    @Override
    GroupEntity update(GroupEntity group) {
        sessionFactory.currentSession.merge(group)
        sessionFactory.currentSession.flush()

        return get(group.id)
    }

    @Override
    List<GroupEntity> search(GroupGetOption getOption) {
        String query = 'select * from group_entity where value like ' +
                (getOption.value == null ? '\'%%\'' : '\'%' + getOption.value + '%\'') +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())
        List entities = sessionFactory.currentSession.createSQLQuery(query).addEntity(GroupEntity).list()

        return entities
    }
}