package com.junbo.authorization.db.repository
import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.RoleId
import com.junbo.langur.core.promise.Promise
/**
 * Created by Zhanxin on 5/16/2014.
 */
class RoleAssignmentRepositoryCloudantImpl extends CloudantClient<RoleAssignment> implements RoleAssignmentRepository {

    @Override
    Promise<RoleAssignment> get(RoleAssignmentId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<RoleAssignment> create(RoleAssignment roleAssignment) {
        return cloudantPost(roleAssignment)
    }

    @Override
    Promise<RoleAssignment> update(RoleAssignment model) {
        return cloudantPut(model)
    }

    @Override
    Promise<Void> delete(RoleAssignmentId id) {
        return cloudantDelete(id.toString())
    }

    @Override
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeIdType, String assigneeId) {
        String key = "$roleId:$assigneeIdType:$assigneeId"
        return queryView('by_role_id', key).then { List<RoleAssignment> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }
}
