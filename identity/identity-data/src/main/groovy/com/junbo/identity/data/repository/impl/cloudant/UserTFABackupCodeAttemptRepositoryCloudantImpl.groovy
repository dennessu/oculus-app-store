package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.data.repository.UserTFABackupCodeAttemptRepository
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFABackupCodeAttemptRepositoryCloudantImpl extends CloudantClient<UserTFABackupCodeAttempt>
        implements UserTFABackupCodeAttemptRepository  {

    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserTFABackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> create(UserTFABackupCodeAttempt entity) {
        if (entity.id == null) {
            entity.id = new UserTFABackupCodeAttemptId(idGenerator.nextId(entity.userId.value))
        }
        return super.cloudantPost(entity)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> update(UserTFABackupCodeAttempt entity) {
        return super.cloudantPut(entity)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> get(UserTFABackupCodeAttemptId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeAttemptId id) {
        return super.cloudantDelete(id.toString())
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
