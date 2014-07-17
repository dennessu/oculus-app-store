package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by minhao on 4/12/14.
 */
@CompileStatic
class UserSecurityQuestionRepositoryCloudantImpl extends CloudantClient<UserSecurityQuestion>
        implements UserSecurityQuestionRepository {

    @Override
    Promise<UserSecurityQuestion> create(UserSecurityQuestion entity) {
        entity.answer = null
        return cloudantPost(entity)
    }

    @Override
    Promise<UserSecurityQuestion> update(UserSecurityQuestion entity, UserSecurityQuestion oldEntity) {
        entity.answer = null
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<UserSecurityQuestion> get(UserSecurityQuestionId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserSecurityQuestion>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionId id) {
        return cloudantDelete(id.toString())
    }
}
