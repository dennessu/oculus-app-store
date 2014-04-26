package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.GenderValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserGender
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class GenderValidatorImpl implements GenderValidator {

    private List<String> allowedValues;

    @Override
    void validate(UserGender userGender) {
        if (userGender.value != null) {
            if (!(userGender.value in allowedValues)) {
                throw AppErrors.INSTANCE.fieldInvalid('value', allowedValues.join(',')).exception()
            }
        }
    }

    @Required
    void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues
    }
}
