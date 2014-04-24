package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserOptinId
import com.junbo.identity.data.repository.UserCommunicationRepository
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-11.
 */
@CompileStatic
class UserCommunicationRepositoryCloudantImpl extends CloudantClient<UserOptin> implements UserCommunicationRepository {
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
        if (entity.id == null) {
            entity.id = new UserOptinId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserOptin)super.cloudantPost(entity))
    }

    @Override
    Promise<UserOptin> update(UserOptin entity) {
        return Promise.pure((UserOptin)super.cloudantPut(entity))
    }

    @Override
    Promise<UserOptin> get(UserOptinId id) {
        return Promise.pure((UserOptin)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserOptin>> search(UserOptinListOptions getOption) {
        def result = []
        if (getOption.userId != null) {
            if (getOption.communicationId != null) {
                result = super.queryView('by_user_id_communication_id',
                        "${getOption.userId.value}:${getOption.communicationId.value}",
                        getOption.limit, getOption.offset, false)
            } else {
                result = super.queryView('by_user_id', getOption.userId.value.toString(),
                        getOption.limit, getOption.offset, false)
            }
        } else if (getOption.communicationId != null) {
            result = super.queryView('by_communication_id', getOption.communicationId.value.toString(),
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
                    'by_communication_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.communicationId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_communication_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                '  emit(doc.userId.value.toString() + \':\' + doc.communicationId.value.toString(), ' +
                                'doc._id)' +
                                '}',
                            resultClass: String)
            ]
    )
}
