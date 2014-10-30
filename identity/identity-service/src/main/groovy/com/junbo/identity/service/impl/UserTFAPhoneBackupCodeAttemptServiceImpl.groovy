package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.data.repository.UserTFAPhoneBackupCodeAttemptRepository
import com.junbo.identity.service.UserTFAPhoneBackupCodeAttemptService
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserTFAPhoneBackupCodeAttemptServiceImpl implements UserTFAPhoneBackupCodeAttemptService {
    private UserTFAPhoneBackupCodeAttemptRepository userTFAPhoneBackupCodeAttemptRepository

    @Override
    Promise<UserTFABackupCodeAttempt> get(UserTFABackupCodeAttemptId id) {
        return userTFAPhoneBackupCodeAttemptRepository.get(id)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> create(UserTFABackupCodeAttempt model) {
        return userTFAPhoneBackupCodeAttemptRepository.create(model)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> update(UserTFABackupCodeAttempt model, UserTFABackupCodeAttempt oldModel) {
        return userTFAPhoneBackupCodeAttemptRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeAttemptId id) {
        return userTFAPhoneBackupCodeAttemptRepository.delete(id)
    }

    @Override
    Promise<List<UserTFABackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userTFAPhoneBackupCodeAttemptRepository.searchByUserId(userId, limit, offset)
    }

    @Required
    void setUserTFAPhoneBackupCodeAttemptRepository(UserTFAPhoneBackupCodeAttemptRepository userTFAPhoneBackupCodeAttemptRepository) {
        this.userTFAPhoneBackupCodeAttemptRepository = userTFAPhoneBackupCodeAttemptRepository
    }
}
