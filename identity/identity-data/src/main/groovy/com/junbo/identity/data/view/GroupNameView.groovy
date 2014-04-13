/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.view

import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/2/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class GroupNameView implements EntityView<Long, GroupEntity, String>  {

    @Override
    String getName() {
        return 'group_groupname'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<GroupEntity> getEntityType() {
        return GroupEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(GroupEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.name != null
    }

    @Override
    List<String> mapEntity(GroupEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.name]
    }
}
