/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.filter

import com.junbo.authorization.filter.AbstractResourceFilter
import com.junbo.authorization.spec.model.Role
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * RoleFilter.
 */
@CompileStatic
class RoleFilter extends AbstractResourceFilter<Role> {

    @Autowired
    protected SelfMapper selfMapper

    @Override
    protected Role filter(Role role, MappingContext context) {
        return selfMapper.filterRole(role, context)
    }

    @Override
    protected Role merge(Role source, Role base, MappingContext context) {
        return selfMapper.mergeRole(source, base, context)
    }
}
