package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
interface UserTosValidator {
    Promise<UserTosAgreement> validateForGet(UserTosAgreementId userTosId)
    Promise<Void> validateForSearch(UserTosAgreementListOptions options)
    Promise<Void> validateForCreate(UserTosAgreement userTos)
    Promise<Void> validateForUpdate(UserTosAgreementId userTosId, UserTosAgreement userTos, UserTosAgreement oldUserTos)
}
