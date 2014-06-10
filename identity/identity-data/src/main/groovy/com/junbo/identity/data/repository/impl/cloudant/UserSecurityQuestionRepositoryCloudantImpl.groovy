package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
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
        return super.cloudantPost(entity)
    }

    @Override
    Promise<UserSecurityQuestion> update(UserSecurityQuestion entity) {
        entity.answer = null
        return super.cloudantPut(entity)
    }

    @Override
    Promise<UserSecurityQuestion> get(UserSecurityQuestionId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserSecurityQuestion>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionId id) {
        return super.cloudantDelete(id.toString())
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
