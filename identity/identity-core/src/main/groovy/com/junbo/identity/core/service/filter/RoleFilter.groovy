/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.Role
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * RoleFilter.
 */
@CompileStatic
class RoleFilter extends ResourceFilterImpl<Role> {
    @Override
    protected Role filter(Role role, MappingContext context) {
        return selfMapper.filterRole(role, context)
    }

    @Override
    protected Role merge(Role source, Role base, MappingContext context) {
        return selfMapper.mergeRole(source, base, context)
    }
}
