package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserCredentialVerifyAttemptId
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
        entity.id = new UserCredentialVerifyAttemptId(idGenerator.nextId(entity.userId.value))
        super.cloudantPost(entity)
        return get((UserCredentialVerifyAttemptId)entity.id)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> update(UserCredentialVerifyAttempt entity) {
        super.cloudantPut(entity)
        return get((UserCredentialVerifyAttemptId)entity.id)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id) {
        return Promise.pure((UserCredentialVerifyAttempt)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> search(UserCredentialAttemptListOptions getOption) {
        def result = []
        def list = super.queryView('by_user_id', getOption.userId.value.toString())
        if (getOption.type != null) {
            list.each { UserCredentialVerifyAttempt attempt ->
                if (getOption.type == attempt.type) {
                    result.add(attempt)
                }
            }
        }
        else {
            result.addAll(list)
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Void> delete(UserCredentialVerifyAttemptId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.user.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
