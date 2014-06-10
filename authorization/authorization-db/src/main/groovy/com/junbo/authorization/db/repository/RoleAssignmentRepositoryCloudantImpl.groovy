package com.junbo.authorization.db.repository

import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.RoleId
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import org.springframework.beans.factory.annotation.Required

/**
 * Created by Zhanxin on 5/16/2014.
 */
class RoleAssignmentRepositoryCloudantImpl extends CloudantClient<RoleAssignment> implements RoleAssignmentRepository {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    Promise<RoleAssignment> get(RoleAssignmentId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<RoleAssignment> create(RoleAssignment roleAssignment) {
        if (roleAssignment.id == null) {
            roleAssignment.id = new RoleAssignmentId(idGenerator.nextId())
        }
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
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeIdType, Long assigneeId) {
        String key = "$roleId:$assigneeIdType:$assigneeId"
        return queryView('by_role_id', key).then { List<RoleAssignment> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    protected CloudantViews views = new CloudantViews(
        views: [
            'by_role_id': new CloudantViews.CloudantView(
                map: 'function(doc) {' +
                    '  emit(doc.roleId + \':\' + doc.assigneeType + \':\' + doc.assigneeId, doc._id)' +
                    '}',
                resultClass: String)
        ]
    )
}
