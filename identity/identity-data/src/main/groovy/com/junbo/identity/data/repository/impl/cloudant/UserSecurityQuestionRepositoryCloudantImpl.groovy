package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 4/12/14.
 */
@CompileStatic
class UserSecurityQuestionRepositoryCloudantImpl extends CloudantClient<UserSecurityQuestion>
        implements UserSecurityQuestionRepository {
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
    Promise<UserSecurityQuestion> create(UserSecurityQuestion entity) {
        if (entity.id == null) {
            entity.id = new UserSecurityQuestionId(idGenerator.nextId(entity.userId.value))
        }
        entity.answer = null
        return Promise.pure((UserSecurityQuestion)super.cloudantPost(entity))
    }

    @Override
    Promise<UserSecurityQuestion> update(UserSecurityQuestion entity) {
        entity.answer = null
        return Promise.pure((UserSecurityQuestion)super.cloudantPut(entity))
    }

    @Override
    Promise<UserSecurityQuestion> get(UserSecurityQuestionId id) {
        return Promise.pure((UserSecurityQuestion)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserSecurityQuestion>> search(UserSecurityQuestionListOptions getOption) {
        def list = super.queryView('by_user_id', getOption.userId.value.toString(),
                getOption.limit, getOption.offset, false)
        return Promise.pure(list)
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
        views: [
            'by_user_id': new CloudantViews.CloudantView(
                map: 'function(doc) {' +
                    '    emit(doc.userId, doc._id)' +
                    '}',
                resultClass: String)
        ]
    )
}
