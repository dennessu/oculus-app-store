package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserOptinId
import com.junbo.identity.data.repository.UserOptinRepository
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-11.
 */
class UserOptinRepositoryCloudantImpl extends CloudantClient<UserOptin> implements UserOptinRepository {
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
    Promise<UserOptin> create(UserOptin entity) {
        entity.id = new UserOptinId(idGenerator.nextId(entity.userId.value))
        super.cloudantPost(entity)
        return get((UserOptinId)entity.id)
    }

    @Override
    Promise<UserOptin> update(UserOptin entity) {
        super.cloudantPut(entity)
        return get((UserOptinId)entity.id)
    }

    @Override
    Promise<UserOptin> get(UserOptinId id) {
        return Promise.pure((UserOptin)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserOptin>> search(UserOptinListOptions getOption) {
        def result = []
        if (getOption.userId != null) {
            if (getOption.type != null) {
                result = super.queryView('by_user_id_type',
                        "${getOption.userId.value}:${getOption.type}",
                        getOption.limit, getOption.offset, false)
            } else {
                result = super.queryView('by_user_id', getOption.userId.toString(),
                        getOption.limit, getOption.offset, false)
            }
        } else if (getOption.type != null) {
            result = super.queryView('by_type', getOption.type,
                    getOption.limit, getOption.offset, false)
        }

        return Promise.pure(result)
    }

    @Override
    Promise<Void> delete(UserOptinId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_type': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.type, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_type': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString() + \':\' + doc.type, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
