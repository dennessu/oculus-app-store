package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.DeleteMethod
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.dualwrite.annotations.WriteMethod
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

    Promise<List<UserSecurityQuestion>> searchByUserId(UserId userId, Integer limit, Integer offset)
}