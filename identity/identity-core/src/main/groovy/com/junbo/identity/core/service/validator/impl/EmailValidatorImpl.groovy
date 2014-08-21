package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.core.service.validator.EmailValidator
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by xiali_000 on 2014/8/20.
 */
@CompileStatic
class EmailValidatorImpl implements EmailValidator {
    private List<Pattern> allowedEmailPatterns
    private Integer minEmailLength
    private Integer maxEmailLength

    @Override
    void validateEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('email').exception()
        }

        if (email.length() < minEmailLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('email', minEmailLength).exception()
        }
        if (email.length() > maxEmailLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('email', maxEmailLength).exception()
        }

        if (!allowedEmailPatterns.any {
            Pattern pattern -> pattern.matcher(email.toLowerCase(Locale.ENGLISH)).matches()
        }) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('email').exception()
        }
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
