package com.junbo.identity.service

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface UserCredentialVerifyAttemptService {
    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id)

    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt model)

    Promise<UserCredentialVerifyAttempt> update(UserCredentialVerifyAttempt model, UserCredentialVerifyAttempt oldModel)

    Promise<Void> delete(UserCredentialVerifyAttemptId id)

    Promise<List<UserCredentialVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserCredentialVerifyAttempt>> searchByUserIdAndCredentialTypeAndInterval(UserId userId, String type, Long fromTimeStamp,
                                                                                          Integer limit, Integer offset)

    Promise<Integer> searchByUserIdAndCredentialTypeAndIntervalCount(UserId userId, String type, Long fromTimeStamp, Integer limit, Integer offset)

    Promise<List<UserCredentialVerifyAttempt>> searchNonLockPeriodHistory(UserId userId, String type, Long fromTimeStamp, Integer limit, Integer offset)

    Promise<List<UserCredentialVerifyAttempt>> searchByIPAddressAndCredentialTypeAndInterval(String ipAddress, String type, Long fromTimeStamp,
                                                                                             Integer limit, Integer offset)

    Promise<Integer> searchByIPAddressAndCredentialTypeAndIntervalCount(String ipAddress, String type, Long fromTimeStamp, Integer limit, Integer offset)
}