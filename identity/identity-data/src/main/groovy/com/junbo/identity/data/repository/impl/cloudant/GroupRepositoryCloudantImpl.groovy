package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.GroupId
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.spec.v1.model.Group
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class GroupRepositoryCloudantImpl extends CloudantClient<Group> implements GroupRepository {
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
    Promise<Group> get(GroupId groupId) {
        return Promise.pure((Group)super.cloudantGet(groupId.toString()))
    }

    @Override
    Promise<Group> create(Group group) {
        group.id = new GroupId(idGenerator.nextIdByShardId(shardAlgorithm.shardId()))
        super.cloudantPost(group)
        return get((GroupId)group.id)
    }

    @Override
    Promise<Group> update(Group group) {
        super.cloudantPut(group)
        return get((GroupId)group.id)
    }

    @Override
    Promise<Group> searchByName(String name) {
        def list = super.queryView('by_name', name)
        return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
    }

    @Override
    Promise<Void> delete(GroupId id) {
        throw new IllegalStateException('delete group not support')
    }
    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_name': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.name, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }
}
