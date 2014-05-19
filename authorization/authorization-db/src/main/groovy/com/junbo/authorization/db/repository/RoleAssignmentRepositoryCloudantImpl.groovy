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
        return Promise.pure((RoleAssignment)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<RoleAssignment> create(RoleAssignment roleAssignment) {
        if (roleAssignment.id == null) {
            roleAssignment.id = new RoleAssignmentId(idGenerator.nextId())
        }
        return Promise.pure((RoleAssignment)super.cloudantPost(roleAssignment))
    }

    @Override
    Promise<RoleAssignment> update(RoleAssignment model) {
        return Promise.pure((RoleAssignment)super.cloudantPut(model))
    }

    @Override
    Promise<Void> delete(RoleAssignmentId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    @Override
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeIdType, Long assigneeId) {
        String key = "$roleId:$assigneeIdType:$assigneeId"
        def list = super.queryView('by_role_id', key)
        return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    protected CloudantViews views = new CloudantViews(
        views: [
            'by_role_id': new CloudantViews.CloudantView(
                map: 'function(doc) {' +
                    '  emit(doc.roleId + \':\' + doc.assigneeIdType + \':\' + doc.assigneeId, doc._id)' +
                    '}',
                resultClass: String)
        ]
    )
}
