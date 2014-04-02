/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.GroupDAO
import com.junbo.identity.data.entity.group.GroupEntity
import org.apache.commons.collections.CollectionUtils

/**
 * Created by liangfu on 3/14/14.
 */
class GroupDAOImpl extends ShardedDAOBase implements GroupDAO {

    @Override
    GroupEntity get(Long groupId) {
        return (GroupEntity)currentSession().get(GroupEntity, groupId)
    }

    @Override
    GroupEntity save(GroupEntity group) {
        currentSession().save(group)
        currentSession().flush()
        return get(group.id)
    }

    @Override
    GroupEntity update(GroupEntity group) {
        currentSession().merge(group)
        currentSession().flush()

        return get(group.id)
    }

    @Override
    // Todo:    Liangfu:    Need to implement this after Kgu's new sharding
    Long findIdByName(String name) {
        GroupEntity example = new GroupEntity()
        example.setName(name)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            return CollectionUtils.isEmpty(ids) ? null : (Long)(ids.get(0))
        }

        throw new RuntimeException()
    }
}