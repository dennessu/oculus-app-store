/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.db.repository

import com.junbo.common.id.RoleId
import com.junbo.authorization.spec.model.Role
import com.junbo.langur.core.promise.Promise

/**
 * Created by Shenhua on 5/13/2014.
 */
public interface RoleRepository {

    Promise<Role> create(Role model)

    Promise<Role> update(Role model, Role oldModel)

    Promise<Void> delete(RoleId id)

    Promise<Role> get(RoleId id)

    Promise<Role> findByRoleName(String roleName, String targetType, String filterType,
                                 String filterLinkIdType, String filterLinkId)
}
