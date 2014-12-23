package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserSecurityQuestionAttemptService {
    Promise<UserSecurityQuestionVerifyAttempt> get(UserSecurityQuestionVerifyAttemptId id)

    Promise<UserSecurityQuestionVerifyAttempt> create(UserSecurityQuestionVerifyAttempt model)

    Promise<UserSecurityQuestionVerifyAttempt> update(UserSecurityQuestionVerifyAttempt model,
                                                      UserSecurityQuestionVerifyAttempt oldModel)

    Promise<Void> delete(UserSecurityQuestionVerifyAttemptId id)

    Promise<List<UserSecurityQuestionVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserSecurityQuestionVerifyAttempt>> searchByUserIdAndSecurityQuestionId(UserId userId,
                                     UserSecurityQuestionId userSecurityQuestionId, Integer limit, Integer offset)
}
