/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.RoleId
import com.junbo.identity.spec.v1.model.RoleAssignment
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * RoleAssignmentRepository.
 */
@CompileStatic
interface RoleAssignmentRepository {
    Promise<RoleAssignment> create(RoleAssignment roleAssignment)

    Promise<RoleAssignment> get(RoleAssignmentId id)

    Promise<RoleAssignment> update(RoleAssignment roleAssignment)

    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeType, Long assigneeId)
}
