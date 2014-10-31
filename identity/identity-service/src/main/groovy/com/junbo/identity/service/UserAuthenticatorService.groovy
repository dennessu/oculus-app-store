package com.junbo.identity.service

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface UserAuthenticatorService {
    Promise<UserAuthenticator> get(UserAuthenticatorId id)

    Promise<UserAuthenticator> create(UserAuthenticator model)

    Promise<UserAuthenticator> update(UserAuthenticator model, UserAuthenticator oldModel)

    Promise<Void> delete(UserAuthenticatorId id)

    Promise<Results<UserAuthenticator>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<Results<UserAuthenticator>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset)

    Promise<Results<UserAuthenticator>> searchByExternalId(String externalId, Integer limit, Integer offset)

    Promise<Results<UserAuthenticator>> searchByUserIdAndTypeAndExternalId(UserId userId, String type, String externalId,
                                                                        Integer limit, Integer offset)

    Promise<Results<UserAuthenticator>> searchByUserIdAndExternalId(UserId userId, String externalId, Integer limit,
                                                                 Integer offset)

    Promise<Results<UserAuthenticator>> searchByExternalIdAndType(String externalId, String type, Integer limit,
                                                               Integer offset)
}