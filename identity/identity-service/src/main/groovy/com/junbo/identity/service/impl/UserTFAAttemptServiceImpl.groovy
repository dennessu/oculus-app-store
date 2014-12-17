package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserTFAAttemptRepository
import com.junbo.identity.service.UserTFAAttemptService
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserTFAAttemptServiceImpl implements UserTFAAttemptService {
    private UserTFAAttemptRepository userTFAAttemptRepository

    @Override
    Promise<UserTFAAttempt> get(UserTFAAttemptId id) {
        return userTFAAttemptRepository.get(id)
    }

    @Override
    Promise<UserTFAAttempt> create(UserTFAAttempt model) {
        return userTFAAttemptRepository.create(model)
    }

    @Override
    Promise<UserTFAAttempt> update(UserTFAAttempt model, UserTFAAttempt oldModel) {
        return userTFAAttemptRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserTFAAttemptId id) {
        return userTFAAttemptRepository.delete(id)
    }

    @Override
    Promise<Results<UserTFAAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userTFAAttemptRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<Results<UserTFAAttempt>> searchByUserIdAndUserTFAId(UserId userId, UserTFAId userTFAId, Integer limit, Integer offset) {
        return userTFAAttemptRepository.searchByUserIdAndUserTFAId(userId, userTFAId, limit, offset)
    }

    @Required
    void setUserTFAAttemptRepository(UserTFAAttemptRepository userTFAAttemptRepository) {
        this.userTFAAttemptRepository = userTFAAttemptRepository
    }
}
