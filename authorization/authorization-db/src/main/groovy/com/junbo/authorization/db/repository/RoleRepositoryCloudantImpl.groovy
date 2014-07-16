/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.db.repository
import com.junbo.authorization.spec.model.Role
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.RoleId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by Shenhua on 5/13/2014.
 */
@CompileStatic
class RoleRepositoryCloudantImpl extends CloudantClient<Role> implements RoleRepository {

    @Override
    Promise<Role> create(Role role) {
        return cloudantPost(role)
    }

    @Override
    Promise<Role> get(RoleId roleId) {
        return cloudantGet(roleId.toString())
    }

    @Override
    Promise<Role> update(Role role, Role oldRole) {
        return cloudantPut(role, oldRole)
    }

    @Override
    Promise<Void> delete(RoleId id) {
        throw new IllegalStateException('delete role not support')
    }

    @Override
    Promise<Role> findByRoleName(String roleName, String targetType, String filterType,
                                 String filterLinkIdType, String filterLinkId) {
        String key = "$roleName:$targetType:$filterType:$filterLinkIdType:$filterLinkId"
        return queryView('by_role_name', key).then { List<Role> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }
}
