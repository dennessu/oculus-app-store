package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.RoleId
import com.junbo.identity.data.repository.RoleAssignmentRepository
import com.junbo.identity.spec.v1.model.RoleAssignment
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 4/13/14.
 */
class RoleAssignmentRepositoryCloudantImpl extends CloudantClient<RoleAssignment> implements RoleAssignmentRepository {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<RoleAssignment> create(RoleAssignment roleAssignment) {
        roleAssignment.id = new RoleAssignmentId(idGenerator.nextIdByShardId(shardAlgorithm.shardId()))
        super.cloudantPost(roleAssignment)
        return get((RoleAssignmentId)roleAssignment.id)
    }

    @Override
    Promise<RoleAssignment> get(RoleAssignmentId id) {
        return Promise.pure((RoleAssignment)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<RoleAssignment> update(RoleAssignment roleAssignment) {
        super.cloudantPut(roleAssignment)
        return get((RoleAssignmentId)roleAssignment.id)
    }

    @Override
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeType, Long assigneeId) {
        return null
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_role_id_assignee': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.roleId.value.toString() + \':\' + doc.assigneeId.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
