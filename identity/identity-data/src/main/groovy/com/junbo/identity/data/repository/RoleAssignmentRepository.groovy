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
import com.junbo.sharding.dualwrite.annotations.DeleteMethod
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.dualwrite.annotations.WriteMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * RoleAssignmentRepository.
 */
@CompileStatic
interface RoleAssignmentRepository extends BaseRepository<RoleAssignment, RoleAssignmentId> {

    @ReadMethod
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeType, Long assigneeId)
}
