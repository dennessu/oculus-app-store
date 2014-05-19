/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.filter

import com.junbo.authorization.spec.model.Role
import groovy.transform.CompileStatic

/**
 * RoleFilter.
 */
@CompileStatic
interface RoleFilter {
    Role filterForPost(Role role)

    Role filterForPut(Role role, Role oldRole)

    Role filterForPatch(Role role, Role oldRole)

    Role filterForGet(Role role)
}
