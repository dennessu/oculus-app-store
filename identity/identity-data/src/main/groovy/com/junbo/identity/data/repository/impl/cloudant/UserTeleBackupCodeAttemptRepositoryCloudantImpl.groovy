package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.identity.data.repository.UserTeleBackupCodeAttemptRepository
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleBackupCodeAttemptRepositoryCloudantImpl extends CloudantClient<UserTeleBackupCodeAttempt>
        implements UserTeleBackupCodeAttemptRepository  {

    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserTeleBackupCodeAttempt>> search(UserTeleBackupCodeAttemptListOptions listOptions) {
        def list = super.queryView('by_user_id', listOptions.userId.value.toString(),
                listOptions.limit, listOptions.offset, false)

        return Promise.pure(list)
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> create(UserTeleBackupCodeAttempt entity) {
        if (entity.id == null) {
            entity.id = new UserTeleBackupCodeAttemptId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserTeleBackupCodeAttempt)super.cloudantPost(entity))
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> update(UserTeleBackupCodeAttempt entity) {
        return Promise.pure((UserTeleBackupCodeAttempt)super.cloudantPut(entity))
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> get(UserTeleBackupCodeAttemptId id) {
        return Promise.pure((UserTeleBackupCodeAttempt)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserTeleBackupCodeAttemptId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
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
