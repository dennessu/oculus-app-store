package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.PassportValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserPassport
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class PassportValidatorImpl implements PassportValidator {

    private Integer minPassportLength
    private Integer maxPassportLength

    @Override
    void validate(UserPassport userPassport) {
        if (userPassport.value != null) {
            if (userPassport.value.length() > maxPassportLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value', maxPassportLength).exception()
            }
            if (userPassport.value.length() < minPassportLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value', minPassportLength).exception()
            }
        }
    }

    @Required
    void setMinPassportLength(Integer minPassportLength) {
        this.minPassportLength = minPassportLength
    }

    @Required
    void setMaxPassportLength(Integer maxPassportLength) {
        this.maxPassportLength = maxPassportLength
    }
}
