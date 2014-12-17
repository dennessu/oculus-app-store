package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserSecurityQuestionService {
    Promise<UserSecurityQuestion> get(UserSecurityQuestionId id)

    Promise<UserSecurityQuestion> create(UserSecurityQuestion model)

    Promise<UserSecurityQuestion> update(UserSecurityQuestion model, UserSecurityQuestion oldModel)

    Promise<Void> delete(UserSecurityQuestionId id)

    Promise<Results<UserSecurityQuestion>> searchByUserId(UserId userId, Integer limit, Integer offset)
}