package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by minhao on 4/12/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptRepositoryCloudantImpl extends CloudantClient<UserSecurityQuestionVerifyAttempt>
        implements UserSecurityQuestionAttemptRepository {

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> create(UserSecurityQuestionVerifyAttempt entity) {
        entity.value = null
        return super.cloudantPost(entity)
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> get(UserSecurityQuestionVerifyAttemptId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> update(UserSecurityQuestionVerifyAttempt model) {
        throw new IllegalStateException('update user security question attempt not support')
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionVerifyAttemptId id) {
        throw new IllegalStateException('delete user security question attempt not support')
    }

    @Override
    Promise<List<UserSecurityQuestionVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserSecurityQuestionVerifyAttempt>> searchByUserIdAndSecurityQuestionId(UserId userId,
                                     UserSecurityQuestionId userSecurityQuestionId, Integer limit, Integer offset) {
        return super.queryView('by_user_id_security_question_id',
                "${userId.toString()}:${userSecurityQuestionId.toString()}", limit, offset, false)
    }
}
