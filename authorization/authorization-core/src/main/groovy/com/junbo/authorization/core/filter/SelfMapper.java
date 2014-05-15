/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.filter;

import com.junbo.authorization.spec.model.Role;
import com.junbo.authorization.spec.model.RoleAssignment;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;

/**
 * Created by kg on 3/26/2014.
 */
@Mapper(uses = {
})
public interface SelfMapper {

    Role filterRole(Role role, MappingContext context);

    Role mergeRole(Role source, Role base, MappingContext context);

    RoleAssignment filterRoleAssignment(RoleAssignment roleAssignment, MappingContext context);

    RoleAssignment mergeRoleAssignment(RoleAssignment source, RoleAssignment base, MappingContext context);
}
