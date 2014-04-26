package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
interface UserPersonalInfoValidator {

    Promise<UserPersonalInfo> validateForGet(UserPersonalInfoId userPersonalInfoId)
    Promise<Void> validateForSearch(UserPersonalInfoListOptions options)
    Promise<Void> validateForCreate(UserPersonalInfo userPersonalInfo)
    Promise<Void> validateForUpdate(UserPersonalInfo userPii, UserPersonalInfo oldUsePii)
}
