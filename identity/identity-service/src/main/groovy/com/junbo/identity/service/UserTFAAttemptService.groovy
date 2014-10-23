package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserTFAAttemptService {
    Promise<UserTFAAttempt> get(UserTFAAttemptId id)

    Promise<UserTFAAttempt> create(UserTFAAttempt model)

    Promise<UserTFAAttempt> update(UserTFAAttempt model, UserTFAAttempt oldModel)

    Promise<Void> delete(UserTFAAttemptId id)

    Promise<List<UserTFAAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserTFAAttempt>> searchByUserIdAndUserTFAId(UserId userId, UserTFAId userTFAId,
                                                             Integer limit, Integer offset)
}