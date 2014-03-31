package com.junbo.identity.core.service.validator

import com.junbo.common.id.SecurityQuestionId
import com.junbo.identity.spec.model.domaindata.SecurityQuestion
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/27/14.
 */
@CompileStatic
interface SecurityQuestionValidator {
    Promise<Void> validateForGet(SecurityQuestionId securityQuestionId)
    Promise<Void> validateForSearch(SecurityQuestionListOptions options)
    Promise<Void> validateForCreate(SecurityQuestion securityQuestion)
    Promise<Void> validateForUpdate(SecurityQuestionId id, SecurityQuestion securityQuestion,
                                    SecurityQuestion oldSecurityQuestion)
}