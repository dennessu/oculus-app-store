/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.GroupDAO
import com.junbo.identity.data.entity.group.GroupEntity
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.hibernate.Session

/**
 * Created by liangfu on 3/14/14.
 */
@CompileStatic
class GroupDAOImpl extends BaseDAO implements GroupDAO {

    @Override
    GroupEntity get(Long groupId) {
        return (GroupEntity)currentSession(groupId).get(GroupEntity, groupId)
    }

    @Override
    GroupEntity save(GroupEntity group) {
        if (group.id == null) {
            group.id = idGenerator.nextIdByShardId(shardAlgorithm.shardId())
        }
        Session session = currentSession(group.id)
        session.save(group)
        session.flush()
        return get((Long)group.id)
    }

    @Override
    GroupEntity update(GroupEntity group) {
        Session session = currentSession(group.id)
        session.merge(group)
        session.flush()

        return get((Long)group.id)
    }

    @Override
    GroupEntity findIdByName(String name) {
        GroupEntity example = new GroupEntity()
        example.setName(name)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            Long id = CollectionUtils.isEmpty(ids) ? null : (Long)(ids.get(0))
            if (id != null) {
                return get(id)
            }
        }

        return null
    }
}