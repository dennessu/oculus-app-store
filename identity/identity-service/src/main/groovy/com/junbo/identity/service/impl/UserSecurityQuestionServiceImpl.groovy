package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.service.UserSecurityQuestionService
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserSecurityQuestionServiceImpl implements UserSecurityQuestionService {
    private UserSecurityQuestionRepository userSecurityQuestionRepository

    @Override
    Promise<UserSecurityQuestion> get(UserSecurityQuestionId id) {
        return userSecurityQuestionRepository.get(id)
    }

    @Override
    Promise<UserSecurityQuestion> create(UserSecurityQuestion model) {
        return userSecurityQuestionRepository.create(model)
    }

    @Override
    Promise<UserSecurityQuestion> update(UserSecurityQuestion model, UserSecurityQuestion oldModel) {
        return userSecurityQuestionRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserSecurityQuestionId id) {
        return userSecurityQuestionRepository.delete(id)
    }

    @Override
    Promise<List<UserSecurityQuestion>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userSecurityQuestionRepository.searchByUserId(userId, limit, offset)
    }

    @Required
    void setUserSecurityQuestionRepository(UserSecurityQuestionRepository userSecurityQuestionRepository) {
        this.userSecurityQuestionRepository = userSecurityQuestionRepository
    }
}
