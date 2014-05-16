package com.junbo.authorization.core.filter

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.spec.model.Role
import groovy.transform.CompileStatic

/**
 * Created by Zhanxin on 5/16/2014.
 */
@CompileStatic
class RoleFilterImpl implements RoleFilter {
    private static final String ADMIN_RIGHT = 'admin'
    @Override
    Role filterForPost(Role role) {
        role.id = null
        role.adminInfo = null
        return role
    }

    @Override
    Role filterForPut(Role role, Role oldRole) {
        role.adminInfo = oldRole.adminInfo
        return role
    }

    @Override
    Role filterForPatch(Role role, Role oldRole) {
        if (!role.isPropertyAssigned('name')) {
            role.name = oldRole.name
        }

        if (!role.isPropertyAssigned('target')) {
            role.target = oldRole.target
        }

        role.adminInfo = oldRole.adminInfo

        return role
    }

    @Override
    Role filterForGet(Role role) {
        if (!AuthorizeContext.hasRights(ADMIN_RIGHT)) {
            role.adminInfo = null
        }

        return role
    }
}