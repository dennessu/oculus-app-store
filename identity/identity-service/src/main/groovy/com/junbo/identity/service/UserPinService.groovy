package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserPinService {
    Promise<UserPin> get(UserPinId id)

    Promise<UserPin> create(UserPin model)

    Promise<UserPin> update(UserPin model, UserPin oldModel)

    Promise<Void> delete(UserPinId id)

    Promise<List<UserPin>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserPin>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset)
}
