package com.junbo.authorization.core.filter

import com.junbo.authorization.spec.model.RoleAssignment
import groovy.transform.CompileStatic

/**
 * Created by Zhanxin on 5/16/2014.
 */
@CompileStatic
interface RoleAssignmentFilter {
    RoleAssignment filterForPost(RoleAssignment roleAssignment)

    RoleAssignment filterForPut(RoleAssignment roleAssignment, RoleAssignment oldRoleAssignment)

    RoleAssignment filterForPatch(RoleAssignment roleAssignment, RoleAssignment oldRoleAssignment)

    RoleAssignment filterForGet(RoleAssignment roleAssignment)
}
