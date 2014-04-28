package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.SMSValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserSMS
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class SMSValidatorImpl implements SMSValidator {

    private Integer minTextMessageLength
    private Integer maxTextMessageLength

    @Override
    void validate(UserSMS userSMS) {
        if (userSMS.textMessage != null) {
            if (userSMS.textMessage.length() > maxTextMessageLength) {
                throw AppErrors.INSTANCE.fieldTooLong('textMessage', maxTextMessageLength).exception()
            }
            if (userSMS.textMessage.length() < minTextMessageLength) {
                throw AppErrors.INSTANCE.fieldTooShort('textMessage', minTextMessageLength).exception()
            }
        }
    }

    @Required
    void setMinTextMessageLength(Integer minTextMessageLength) {
        this.minTextMessageLength = minTextMessageLength
    }

    @Required
    void setMaxTextMessageLength(Integer maxTextMessageLength) {
        this.maxTextMessageLength = maxTextMessageLength
    }
}
