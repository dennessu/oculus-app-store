package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.UserWhatsAppValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserWhatsApp
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class UserWhatsAppValidatorImpl implements UserWhatsAppValidator {

    private Integer minWhatsAppLength
    private Integer maxWhatsAppLength

    @Override
    void validate(UserWhatsApp userWhatsApp) {
        if (userWhatsApp.value != null) {
            if (userWhatsApp.value.length() > maxWhatsAppLength) {
                throw AppErrors.INSTANCE.fieldTooLong('whatsApp', maxWhatsAppLength).exception()
            }
            if (userWhatsApp.value.length() < minWhatsAppLength) {
                throw AppErrors.INSTANCE.fieldTooShort('whatsApp', maxWhatsAppLength).exception()
            }
        }
    }
}
