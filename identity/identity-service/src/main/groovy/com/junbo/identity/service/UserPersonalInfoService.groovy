package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserPersonalInfoService {
    Promise<UserPersonalInfo> get(UserPersonalInfoId id)

    Promise<UserPersonalInfo> create(UserPersonalInfo model)

    Promise<UserPersonalInfo> update(UserPersonalInfo model, UserPersonalInfo oldModel)

    Promise<Void> delete(UserPersonalInfoId id)

    Promise<List<UserPersonalInfo>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserPersonalInfo>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset)

    Promise<List<UserPersonalInfo>> searchByEmail(String email, Boolean isValidated, Integer limit, Integer offset)

    Promise<List<UserPersonalInfo>> searchByPhoneNumber(String phoneNumber, Boolean isValidated, Integer limit, Integer offset)

    Promise<List<UserPersonalInfo>> searchByName(String name, Integer limit, Integer offset)

    Promise<List<UserPersonalInfo>> searchByUserIdAndValidateStatus(UserId userId, String type, Boolean isValidated,
                                                                    Integer limit, Integer offset)

    Promise<List<UserPersonalInfo>> searchByCanonicalUsername(String canonicalUsername, Integer limit, Integer offset)
}
