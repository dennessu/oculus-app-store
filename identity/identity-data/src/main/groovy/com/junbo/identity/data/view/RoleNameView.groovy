/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.view

import com.junbo.identity.data.entity.role.RoleEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic
import org.springframework.util.Assert

/**
 * RoleNameView.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class RoleNameView implements EntityView<Long, RoleEntity, String> {
    @Override
    String getName() {
        return 'role_role_name'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<RoleEntity> getEntityType() {
        return RoleEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(RoleEntity entity) {
        Assert.notNull(entity, 'entity is null')

        return entity.name != null && entity.resourceType != null && entity.resourceId != null
    }

    @Override
    List<String> mapEntity(RoleEntity entity) {
        Assert.notNull(entity, 'entity is null')

        StringBuilder sb = new StringBuilder()
        sb.append(entity.name)
        sb.append('#')
        sb.append(entity.resourceType)
        sb.append('#')
        sb.append(entity.resourceId)
        if (entity.subResourceType != null) {
            sb.append('#')
            sb.append(entity.subResourceType)
        }

        return [sb.toString()]
    }
}
