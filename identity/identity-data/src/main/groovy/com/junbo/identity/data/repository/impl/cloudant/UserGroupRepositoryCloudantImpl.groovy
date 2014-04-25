package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserGroupId
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
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
    Promise<List<UserGroup>> search(UserGroupListOptions getOption) {
        def result = []
        if (getOption.userId != null) {
            if (getOption.groupId != null) {
                result = super.queryView('by_user_id_group_id',
                        "${getOption.userId.value}:${getOption.groupId.value}",
                        getOption.limit, getOption.offset, false)
            }
            else {
                result = super.queryView('by_user_id', getOption.userId.toString(),
                        getOption.limit, getOption.offset, false)
            }
        }
        else if (getOption.groupId != null) {
            result = super.queryView('by_group_id', getOption.groupId.toString(),
                    getOption.limit, getOption.offset, false)
        }
        return Promise.pure(result)
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
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_group_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.groupId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_group_id': new CloudantViews.CloudantView(
                        map: 'function(doc) {' +
                            '  emit(doc.userId.value.toString() + \':\' + doc.groupId.value.toString(), doc._id)' +
                            '}',
                            resultClass: String)
            ]
    )
}
