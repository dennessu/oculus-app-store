package com.junbo.identity.service.impl

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserCredentialVerifyAttemptRepository
import com.junbo.identity.service.UserCredentialVerifyAttemptService
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class UserCredentialVerifyAttemptServiceImpl implements UserCredentialVerifyAttemptService {
    private UserCredentialVerifyAttemptRepository userCredentialVerifyAttemptRepository

    @Override
    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id) {
        return userCredentialVerifyAttemptRepository.get(id)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt model) {
        return userCredentialVerifyAttemptRepository.create(model)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> update(UserCredentialVerifyAttempt model, UserCredentialVerifyAttempt oldModel) {
        return userCredentialVerifyAttemptRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserCredentialVerifyAttemptId id) {
        return userCredentialVerifyAttemptRepository.delete(id)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userCredentialVerifyAttemptRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserIdAndCredentialTypeAndInterval(UserId userId, String type, Long fromTimeStamp, Integer limit, Integer offset) {
        return userCredentialVerifyAttemptRepository.searchByUserIdAndCredentialTypeAndInterval(userId, type, fromTimeStamp, limit, offset)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchNonLockPeriodHistory(UserId userId, String type, Long fromTimeStamp, Integer limit, Integer offset) {
        return userCredentialVerifyAttemptRepository.searchNonLockPeriodHistory(userId, type, fromTimeStamp, limit, offset)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByIPAddressAndCredentialTypeAndInterval(String ipAddress, String type, Long fromTimeStamp, Integer limit, Integer offset) {
        return userCredentialVerifyAttemptRepository.searchByIPAddressAndCredentialTypeAndInterval(ipAddress, type, fromTimeStamp, limit, offset)
    }

    @Required
    void setUserCredentialVerifyAttemptRepository(UserCredentialVerifyAttemptRepository userCredentialVerifyAttemptRepository) {
        this.userCredentialVerifyAttemptRepository = userCredentialVerifyAttemptRepository
    }
}
