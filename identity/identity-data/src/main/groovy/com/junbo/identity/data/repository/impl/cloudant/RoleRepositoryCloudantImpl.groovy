package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.RoleId
import com.junbo.identity.data.repository.RoleRepository
import com.junbo.identity.spec.v1.model.Role
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 4/13/14.
 */
@CompileStatic
class RoleRepositoryCloudantImpl extends CloudantClient<Role> implements RoleRepository {
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
    Promise<Role> create(Role role) {
        if (role.id == null) {
            role.id = new RoleId(idGenerator.nextId())
        }
        return Promise.pure((Role)super.cloudantPost(role))
    }

    @Override
    Promise<Role> get(RoleId roleId) {
        return Promise.pure((Role)super.cloudantGet(roleId.toString()))
    }

    @Override
    Promise<Role> update(Role role) {
        return Promise.pure((Role)super.cloudantPut(role))
    }

    @Override
    Promise<Void> delete(RoleId id) {
        throw new IllegalStateException('delete role not support')
    }

    @Override
    Promise<Role> findByRoleName(String roleName, String resourceType, Long resourceId, String subResourceType) {
        return null
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_role_name': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.name, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
