package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserCredentialVerifyAttemptRepository
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserCredentialVerifyAttemptRepositoryCloudantImpl extends CloudantClient<UserCredentialVerifyAttempt>
        implements UserCredentialVerifyAttemptRepository{
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
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt entity) {
        if (entity.id == null) {
            entity.id = new UserCredentialVerifyAttemptId(idGenerator.nextId(entity.userId.value))
        }
        entity.setValue(null)
        return Promise.pure((UserCredentialVerifyAttempt)super.cloudantPost(entity))
    }

    @Override
    Promise<UserCredentialVerifyAttempt> update(UserCredentialVerifyAttempt entity) {
        return Promise.pure((UserCredentialVerifyAttempt)super.cloudantPut(entity))
    }

    @Override
    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id) {
        return Promise.pure((UserCredentialVerifyAttempt)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        def list = super.queryView('by_user_id', userId.toString(), limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserIdAndCredentialType(UserId userId, String type,
                                                                               Integer limit, Integer offset) {
        def list = super.queryView('by_user_id_credential_type', "${userId.toString()}:${type}", limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<Void> delete(UserCredentialVerifyAttemptId id) {
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
            'by_user_id_credential_type': new CloudantViews.CloudantView(
                map: 'function(doc) {' +
                        ' emit(doc.userId + \':\' + doc.type, doc._id)' +
                        '}',
                resultClass: String)
        ]
    )
}
