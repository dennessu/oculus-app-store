/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.view

import com.junbo.identity.data.entity.role.RoleAssignmentEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * RoleAssignmentRoleIdAssigneeView.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class RoleAssignmentRoleIdAssigneeView implements EntityView<Long, RoleAssignmentEntity, String> {
    @Override
    String getName() {
        return 'role_assignment_role_assignee'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<RoleAssignmentEntity> getEntityType() {
        return RoleAssignmentEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(RoleAssignmentEntity entity) {
        Assert.notNull(entity, 'entity is null')

        return entity.roleId != null && StringUtils.hasText(entity.assigneeType) && entity.assigneeId != null
    }

    @Override
    List<String> mapEntity(RoleAssignmentEntity entity) {
        Assert.notNull(entity, 'entity is null')

        StringBuilder sb = new StringBuilder()
        sb.append(entity.roleId)
        sb.append('#')
        sb.append(entity.assigneeType)
        sb.append('#')
        sb.append(entity.assigneeId)

        return [sb.toString()]
    }
}
