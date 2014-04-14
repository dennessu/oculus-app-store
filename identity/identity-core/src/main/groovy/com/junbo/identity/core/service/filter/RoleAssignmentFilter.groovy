/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.RoleAssignment
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * RoleAssignmentFilter.
 */
@CompileStatic
class RoleAssignmentFilter extends ResourceFilterImpl<RoleAssignment> {
    @Override
    protected RoleAssignment filter(RoleAssignment roleAssignment, MappingContext context) {
        return selfMapper.filterRoleAssignment(roleAssignment, context)
    }

    @Override
    protected RoleAssignment merge(RoleAssignment source, RoleAssignment base, MappingContext context) {
        return selfMapper.mergeRoleAssignment(source, base, context)
    }
}
