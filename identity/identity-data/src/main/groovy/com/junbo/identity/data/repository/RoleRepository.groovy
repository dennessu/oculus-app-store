/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.RoleId
import com.junbo.identity.spec.v1.model.Role
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * RoleRepository.
 */
@CompileStatic
interface RoleRepository extends BaseRepository<Role, RoleId> {
    @ReadMethod
    Promise<Role> findByRoleName(String roleName, String resourceType, Long resourceId, String subResourceType)
}
