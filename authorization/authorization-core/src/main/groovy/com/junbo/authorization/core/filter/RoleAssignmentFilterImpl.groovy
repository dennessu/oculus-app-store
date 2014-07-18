package com.junbo.authorization.core.filter

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.spec.model.RoleAssignment
import groovy.transform.CompileStatic

/**
 * Created by Zhanxin on 5/16/2014.
 */
@CompileStatic
class RoleAssignmentFilterImpl implements RoleAssignmentFilter {
    private static final String ADMIN_RIGHT = 'admin'
    @Override
    RoleAssignment filterForPost(RoleAssignment roleAssignment) {
        roleAssignment.id = null
        roleAssignment.adminInfo = null
        return roleAssignment
    }

    @Override
    RoleAssignment filterForPut(RoleAssignment roleAssignment, RoleAssignment oldRoleAssignment) {
        roleAssignment.adminInfo = oldRoleAssignment.adminInfo
        return roleAssignment
    }

    @Override
    RoleAssignment filterForPatch(RoleAssignment roleAssignment, RoleAssignment oldRoleAssignment) {
        roleAssignment.adminInfo = oldRoleAssignment.adminInfo
        return roleAssignment
    }

    @Override
    RoleAssignment filterForGet(RoleAssignment roleAssignment) {
        if (roleAssignment == null) {
            return null
        }

        if (!AuthorizeContext.hasRights(ADMIN_RIGHT)) {
            roleAssignment.adminInfo = null
        }

        return roleAssignment
    }
}
