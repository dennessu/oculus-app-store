package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface UserPasswordService {
    Promise<UserPassword> get(UserPasswordId id)

    Promise<UserPassword> create(UserPassword model)

    Promise<UserPassword> update(UserPassword model, UserPassword oldModel)

    Promise<Void> delete(UserPasswordId id)

    Promise<List<UserPassword>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserPassword>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset)
}
