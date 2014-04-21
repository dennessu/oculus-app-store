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
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * RoleAssignmentRepository.
 */
@CompileStatic
interface RoleAssignmentRepository extends IdentityBaseRepository<RoleAssignment, RoleAssignmentId> {
    @ReadMethod
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeType, Long assigneeId)
}
