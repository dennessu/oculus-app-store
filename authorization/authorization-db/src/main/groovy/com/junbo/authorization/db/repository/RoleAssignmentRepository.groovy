package com.junbo.authorization.db.repository

import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.RoleId
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.DeleteMethod
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.dualwrite.annotations.WriteMethod
import groovy.transform.CompileStatic

/**
 * Created by Zhanxin on 5/16/2014.
 */
@CompileStatic
interface RoleAssignmentRepository {
    @ReadMethod
    Promise<RoleAssignment> get(RoleAssignmentId id);

    @WriteMethod
    Promise<RoleAssignment> create(RoleAssignment model);

    @WriteMethod
    Promise<RoleAssignment> update(RoleAssignment model);

    @DeleteMethod
    Promise<Void> delete(RoleAssignmentId id);

    @ReadMethod
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assignee)
}
