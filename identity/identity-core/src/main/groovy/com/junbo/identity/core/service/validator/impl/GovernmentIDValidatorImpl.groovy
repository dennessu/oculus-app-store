package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.GovernmentIDValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserGovernmentID
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class GovernmentIDValidatorImpl implements GovernmentIDValidator {

    private Integer minGovernmentIDLength
    private Integer maxGovernmentIDLength

    @Override
    void validate(UserGovernmentID userGovernmentID) {
        if (userGovernmentID.value != null) {
            if (userGovernmentID.value.length() > maxGovernmentIDLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value', maxGovernmentIDLength).exception()
            }
            if (userGovernmentID.value.length() < minGovernmentIDLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value', minGovernmentIDLength).exception()
            }
        }
    }

    @Required
    void setMinGovernmentIDLength(Integer minGovernmentIDLength) {
        this.minGovernmentIDLength = minGovernmentIDLength
    }

    @Required
    void setMaxGovernmentIDLength(Integer maxGovernmentIDLength) {
        this.maxGovernmentIDLength = maxGovernmentIDLength
    }
}
