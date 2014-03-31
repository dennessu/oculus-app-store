package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPhoneNumberId
import com.junbo.identity.spec.model.users.UserPhoneNumber
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserPhoneNumberValidator {
    Promise<UserPhoneNumber> validateForGet(UserId userId, UserPhoneNumberId userPhoneNumberId)
    Promise<Void> validateForSearch(UserPhoneNumberListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserPhoneNumber userPhoneNumber)
    Promise<Void> validateForUpdate(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                    UserPhoneNumber userPhoneNumber, UserPhoneNumber oldUserPhoneNumber)
}