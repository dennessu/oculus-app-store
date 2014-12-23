package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.service.UserSecurityQuestionAttemptService
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptServiceImpl implements UserSecurityQuestionAttemptService {
    private UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> get(UserSecurityQuestionVerifyAttemptId id) {
        return userSecurityQuestionAttemptRepository.get(id)
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> create(UserSecurityQuestionVerifyAttempt model) {
        return userSecurityQuestionAttemptRepository.create(model)
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> update(UserSecurityQuestionVerifyAttempt model, UserSecurityQuestionVerifyAttempt oldModel) {
        return userSecurityQuestionAttemptRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionVerifyAttemptId id) {
        return userSecurityQuestionAttemptRepository.delete(id)
    }

    @Override
    Promise<List<UserSecurityQuestionVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userSecurityQuestionAttemptRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserSecurityQuestionVerifyAttempt>> searchByUserIdAndSecurityQuestionId(UserId userId,
                                                                 UserSecurityQuestionId userSecurityQuestionId, Integer limit, Integer offset) {
        return userSecurityQuestionAttemptRepository.searchByUserIdAndSecurityQuestionId(userId, userSecurityQuestionId, limit, offset)
    }

    @Required
    void setUserSecurityQuestionAttemptRepository(UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository) {
        this.userSecurityQuestionAttemptRepository = userSecurityQuestionAttemptRepository
    }
}
