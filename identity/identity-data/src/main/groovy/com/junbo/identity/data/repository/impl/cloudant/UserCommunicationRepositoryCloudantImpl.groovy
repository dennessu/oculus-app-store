package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserCommunicationRepository
import com.junbo.identity.spec.v1.model.UserCommunication
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
class UserCommunicationRepositoryCloudantImpl extends CloudantClient<UserCommunication> implements UserCommunicationRepository {
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
    Promise<UserCommunication> create(UserCommunication entity) {
        if (entity.id == null) {
            entity.id = new UserCommunicationId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserCommunication)super.cloudantPost(entity))
    }

    @Override
    Promise<UserCommunication> update(UserCommunication entity) {
        return Promise.pure((UserCommunication)super.cloudantPut(entity))
    }

    @Override
    Promise<UserCommunication> get(UserCommunicationId id) {
        return Promise.pure((UserCommunication)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserCommunication>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        def list = super.queryView('by_user_id', userId.toString(), limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<List<UserCommunication>> searchByCommunicationId(CommunicationId communicationId, Integer limit,
                                                             Integer offset) {
        def list = super.queryView('by_communication_id', communicationId.toString(), limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<List<UserCommunication>> searchByUserIdAndCommunicationId(UserId userId, CommunicationId communicationId,
                                                                      Integer limit, Integer offset) {
        def list = super.queryView('by_user_id_communication_id', "${userId.toString()}:${communicationId.toString()}",
                limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<Void> delete(UserCommunicationId id) {
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
                    'by_communication_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.communicationId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_communication_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                '  emit(doc.userId + \':\' + doc.communicationId, ' +
                                'doc._id)' +
                                '}',
                            resultClass: String)
            ]
    )
}
