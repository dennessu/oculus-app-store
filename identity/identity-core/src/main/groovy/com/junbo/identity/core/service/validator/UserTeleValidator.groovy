package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleId
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.identity.spec.v1.option.list.UserTeleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
interface UserTeleValidator {
    Promise<UserTeleCode> validateForGet(UserId userId, UserTeleId userTeleId)
    Promise<Void> validateForSearch(UserTeleListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTeleCode userTeleCode)
    Promise<Void> validateForUpdate(UserId userId, UserTeleId userTeleId,
                                    UserTeleCode userTeleCode, UserTeleCode oldUserTeleCode)
}
