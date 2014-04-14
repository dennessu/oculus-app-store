package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 4/12/14.
 */
class UserSecurityQuestionAttemptRepositoryCloudantImpl extends CloudantClient<UserSecurityQuestionVerifyAttempt>
        implements UserSecurityQuestionAttemptRepository {
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
    Promise<UserSecurityQuestionVerifyAttempt> create(UserSecurityQuestionVerifyAttempt entity) {
        entity.id = new UserSecurityQuestionVerifyAttemptId(idGenerator.nextId(entity.userId.value))
        super.cloudantPost(entity)
        return get((UserSecurityQuestionVerifyAttemptId)entity.id)
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> get(UserSecurityQuestionVerifyAttemptId id) {
        return Promise.pure((UserSecurityQuestionVerifyAttempt)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserSecurityQuestionVerifyAttempt>> search(UserSecurityQuestionAttemptListOptions getOption) {
        def list = super.queryView('by_user_id', getOption.userId.value.toString(),
                getOption.limit, getOption.offset, false)
        if (getOption.userSecurityQuestionId != null) {
            list.removeAll { UserSecurityQuestionVerifyAttempt attempt ->
                attempt.userSecurityQuestionId != getOption.userSecurityQuestionId
            }
        }
        return Promise.pure(list)
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
