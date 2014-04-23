package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserTeleAttemptId
import com.junbo.identity.data.repository.UserTeleAttemptRepository
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleAttemptRepositoryCloudantImpl  extends CloudantClient<UserTeleAttempt>
        implements UserTeleAttemptRepository {
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
    Promise<List<UserTeleAttempt>> search(UserTeleAttemptListOptions listOptions) {
        def list = super.queryView('by_user_id', listOptions.userId.toString(),
                listOptions.limit, listOptions.offset, false)

        return Promise.pure(list)
    }

    @Override
    Promise<UserTeleAttempt> create(UserTeleAttempt entity) {
        if (entity.id == null) {
            entity.id = new UserTeleAttemptId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserTeleAttempt)super.cloudantPost(entity))
    }

    @Override
    Promise<UserTeleAttempt> update(UserTeleAttempt entity) {
        return Promise.pure((UserTeleAttempt)super.cloudantPut(entity))
    }

    @Override
    Promise<UserTeleAttempt> get(UserTeleAttemptId id) {
        return Promise.pure((UserTeleAttempt)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserTeleAttemptId id) {
        super.cloudantDelete(id.value)
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
}
