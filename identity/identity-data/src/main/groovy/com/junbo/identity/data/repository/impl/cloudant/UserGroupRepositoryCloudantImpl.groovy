package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.GroupId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-11.
 */
@CompileStatic
class UserGroupRepositoryCloudantImpl extends CloudantClient<UserGroup> implements UserGroupRepository {
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
        return views
    }

    @Override
    Promise<UserGroup> create(UserGroup entity) {
        if (entity.id == null) {
            entity.id = new UserGroupId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserGroup)super.cloudantPost(entity))
    }

    @Override
    Promise<UserGroup> update(UserGroup entity) {
        return Promise.pure((UserGroup)super.cloudantPut(entity))
    }

    @Override
    Promise<UserGroup> get(UserGroupId id) {
        return Promise.pure((UserGroup)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserGroup>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        def list = super.queryView('by_user_id', userId.toString(), limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<List<UserGroup>> searchByGroupId(GroupId groupId, Integer limit, Integer offset) {
        def list = super.queryView('by_group_id', groupId.toString(), limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<List<UserGroup>> searchByUserIdAndGroupId(UserId userId, GroupId groupId, Integer limit, Integer offset) {
        def list = super.queryView('by_user_id_group_id', "${userId.value}:${groupId.value}", limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<Void> delete(UserGroupId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_group_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.groupId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_group_id': new CloudantViews.CloudantView(
                        map: 'function(doc) {' +
                            '  emit(doc.userId + \':\' + doc.groupId, doc._id)' +
                            '}',
                            resultClass: String)
            ]
    )
}
