package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Email
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserEmailValidatorImpl implements PiiValidator {

    private List<Pattern> allowedEmailPatterns
    private Integer minEmailLength
    private Integer maxEmailLength

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.EMAIL.toString()) {
            return true
        }
        return false
    }

    @Override
    void validate(JsonNode value) {
        Email email = ObjectMapperProvider.instance().treeToValue(value, Email)
        if (email.value == null) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }

        if (email.value.length() < minEmailLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', minEmailLength).exception()
        }
        if (email.value.length() > maxEmailLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', maxEmailLength).exception()
        }

        if (!allowedEmailPatterns.any {
            Pattern pattern -> pattern.matcher(email.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }

        // todo:    Need to add logic check to ensure one email can only be added once.
    }

    @Required
    void setAllowedEmailPatterns(List<String> allowedEmailPatterns) {
        this.allowedEmailPatterns = allowedEmailPatterns.collect { String allowedEmailPattern ->
            Pattern.compile(allowedEmailPattern)
        }
    }

    @Required
    void setMinEmailLength(Integer minEmailLength) {
        this.minEmailLength = minEmailLength
    }

    @Required
    void setMaxEmailLength(Integer maxEmailLength) {
        this.maxEmailLength = maxEmailLength
    }
}
