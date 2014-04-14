/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.role.RoleAssignmentEntity
import groovy.transform.CompileStatic

/**
 * RoleAssignmentDAO.
 */
@CompileStatic
interface RoleAssignmentDAO {
    RoleAssignmentEntity create(RoleAssignmentEntity entity)

    RoleAssignmentEntity get(Long roleAssignmentId)

    RoleAssignmentEntity update(RoleAssignmentEntity entity)

    RoleAssignmentEntity findByRoleIdAssignee(Long roleId, String assigneeType, Long assigneeId)
}
