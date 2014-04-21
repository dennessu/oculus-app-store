/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.RoleId
import com.junbo.identity.spec.v1.model.Role
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * RoleRepository.
 */
@CompileStatic
interface RoleRepository extends IdentityBaseRepository<Role, RoleId> {
    Promise<Role> findByRoleName(String roleName, String resourceType, Long resourceId, String subResourceType)
}
