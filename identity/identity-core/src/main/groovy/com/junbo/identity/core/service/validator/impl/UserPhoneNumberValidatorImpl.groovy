package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.PhoneNumber
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPhoneNumberValidatorImpl implements PiiValidator {
    private Integer minValueLength
    private Integer maxValueLength
    private List<Pattern> allowedValuePatterns

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.PHONE.toString()) {
            return true
        }
        return false
    }

    @Override
    void validate(JsonNode value) {
        PhoneNumber phoneNumber = ObjectMapperProvider.instance().treeToValue(value, PhoneNumber)
        if (phoneNumber.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }

        if (phoneNumber.value.length() > maxValueLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', maxValueLength).exception()
        }
        if (phoneNumber.value.length() < minValueLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', minValueLength).exception()
        }

        if (!allowedValuePatterns.any {
            Pattern pattern -> pattern.matcher(phoneNumber.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }
    }

    @Required
    void setAllowedValuePatterns(List<String> allowedValuePatterns) {
        this.allowedValuePatterns = allowedValuePatterns.collect {
            String allowedValuePattern -> Pattern.compile(allowedValuePattern)
        }
    }

    @Required
    void setMinValueLength(Integer minValueLength) {
        this.minValueLength = minValueLength
    }

    @Required
    void setMaxValueLength(Integer maxValueLength) {
        this.maxValueLength = maxValueLength
    }
}
