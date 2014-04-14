package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.UserPhoneNumberValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserPhoneNumber
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPhoneNumberValidatorImpl implements UserPhoneNumberValidator {
    private Integer typeMinLength
    private Integer typeMaxLength

    private Integer valueMinLength
    private Integer valueMaxLength
    private List<Pattern> allowedValuePatterns

    @Override
    void validate(UserPhoneNumber userPhoneNumber) {
        if (userPhoneNumber == null) {
            throw new IllegalArgumentException('userPhoneNumber is null')
        }
        if (userPhoneNumber.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (userPhoneNumber.type.size() > typeMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('type', typeMaxLength).exception()
        }
        if (userPhoneNumber.type.size() < typeMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('type', typeMinLength).exception()
        }

        if (userPhoneNumber.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        if (userPhoneNumber.value.size() > valueMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', valueMaxLength).exception()
        }
        if (userPhoneNumber.value.size() < valueMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', valueMinLength).exception()
        }

        if (!allowedValuePatterns.any {
            Pattern pattern -> pattern.matcher(userPhoneNumber.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }

        if (userPhoneNumber.verified == null) {
            throw AppErrors.INSTANCE.fieldRequired('verified').exception()
        }
    }

    @Required
    void setTypeMinLength(Integer typeMinLength) {
        this.typeMinLength = typeMinLength
    }

    @Required
    void setTypeMaxLength(Integer typeMaxLength) {
        this.typeMaxLength = typeMaxLength
    }

    @Required
    void setValueMinLength(Integer valueMinLength) {
        this.valueMinLength = valueMinLength
    }

    @Required
    void setValueMaxLength(Integer valueMaxLength) {
        this.valueMaxLength = valueMaxLength
    }

    @Required
    void setAllowedValuePatterns(List<String> allowedValuePatterns) {
        this.allowedValuePatterns = allowedValuePatterns.collect {
            String allowedValuePattern -> Pattern.compile(allowedValuePattern)
        }
    }
}
