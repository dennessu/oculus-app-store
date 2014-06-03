package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.data.repository.UserTFAAttemptRepository
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFAAttemptRepositoryCloudantImpl extends CloudantClient<UserTFAAttempt>
        implements UserTFAAttemptRepository {
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
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserTFAAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        def list = super.queryView('by_user_id', userId.toString(), limit, offset, false)

        return Promise.pure(list)
    }

    @Override
    Promise<List<UserTFAAttempt>> searchByUserIdAndUserTFAId(UserId userId, UserTFAId userTFAId,
                                                               Integer limit, Integer offset) {
        def list = super.queryView('by_user_id_tfa_id', "${userId.toString()}:${userTFAId.toString()}", limit,
                offset, false)

        return Promise.pure(list)
    }

    @Override
    Promise<UserTFAAttempt> create(UserTFAAttempt entity) {
        if (entity.id == null) {
            entity.id = new UserTFAAttemptId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserTFAAttempt)super.cloudantPost(entity))
    }

    @Override
    Promise<UserTFAAttempt> update(UserTFAAttempt entity) {
        return Promise.pure((UserTFAAttempt)super.cloudantPut(entity))
    }

    @Override
    Promise<UserTFAAttempt> get(UserTFAAttemptId id) {
        return Promise.pure((UserTFAAttempt)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserTFAAttemptId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                'by_user_id_tfa_id': new CloudantViews.CloudantView(
                    map: 'function(doc) {' +
                            '  emit(doc.userId + \':\' + doc.userTFAId , doc._id)' +
                            '}',
                    resultClass: String),
                'by_user_id': new CloudantViews.CloudantView(
                    map: 'function(doc) {' +
                            '  emit(doc.userId, doc._id)' +
                            '}',
                    resultClass: String)
            ]
    )
}
