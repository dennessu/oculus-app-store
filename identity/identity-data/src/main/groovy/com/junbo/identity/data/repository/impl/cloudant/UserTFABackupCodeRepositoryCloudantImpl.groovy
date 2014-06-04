package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.identity.data.repository.UserTFABackupCodeRepository
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFABackupCodeRepositoryCloudantImpl extends CloudantClient<UserTFABackupCode>
        implements UserTFABackupCodeRepository  {

    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserTFABackupCode>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        def list = super.queryView('by_user_id', userId.toString(), limit, offset, false)

        return Promise.pure(list)
    }

    @Override
    Promise<List<UserTFABackupCode>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit,
                                                                    Integer offset) {
        def list = super.queryView('by_user_id_active', "${userId.toString()}:${active}", limit, offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<UserTFABackupCode> create(UserTFABackupCode entity) {
        if (entity.id == null) {
            entity.id = new UserTFABackupCodeId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserTFABackupCode)super.cloudantPost(entity))
    }

    @Override
    Promise<UserTFABackupCode> update(UserTFABackupCode entity) {
        return Promise.pure((UserTFABackupCode)super.cloudantPut(entity))
    }

    @Override
    Promise<UserTFABackupCode> get(UserTFABackupCodeId id) {
        return Promise.pure((UserTFABackupCode)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeId id) {
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
                    'by_user_id_active': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId + \':\' + doc.active.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
