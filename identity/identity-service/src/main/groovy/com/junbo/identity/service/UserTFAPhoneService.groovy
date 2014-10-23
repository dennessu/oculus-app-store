package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserTFAPhoneService {
    Promise<UserTFA> get(UserTFAId id)

    Promise<UserTFA> create(UserTFA model)

    Promise<UserTFA> update(UserTFA model, UserTFA oldModel)

    Promise<Void> delete(UserTFAId id)

    Promise<List<UserTFA>> searchTFACodeByUserIdAndPersonalInfoId(UserId userId, UserPersonalInfoId personalInfoId,
                                                                  Integer limit, Integer offset)

    Promise<List<UserTFA>> searchTFACodeByUserIdAndPIIAfterTime(UserId userId, UserPersonalInfoId personalInfoId,
                                                                Integer limit, Integer offset, Long startTimeOffset)
}