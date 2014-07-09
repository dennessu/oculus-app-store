package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.core.service.validator.UsernameValidator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Check username minimum and maximum length
 * Check allowed pattern and not allowed pattern
 * Created by kg on 3/17/14.
 */
@CompileStatic
class UsernameValidatorImpl implements UsernameValidator {

    private Integer minLength

    private Integer maxLength

    private List<Pattern> disallowedPatterns

    private List<Pattern> allowedPatterns

    @Required
    void setMinLength(Integer minLength) {
        this.minLength = minLength
    }

    @Required
    void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength
    }

    @Required
    void setDisallowedPatterns(List<String> disallowedPatterns) {
        this.disallowedPatterns = disallowedPatterns.collect { String pattern -> Pattern.compile(pattern) }
    }

    @Required
    void setAllowedPatterns(List<String> allowedPatterns) {
        this.allowedPatterns = allowedPatterns.collect { String pattern -> Pattern.compile(pattern) }
    }

    void validateUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException('username is null')
        }

        if (username.length() < minLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('username', minLength).exception()
        }

        if (username.length() > maxLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('username', maxLength).exception()
        }

        if (disallowedPatterns.any { Pattern pattern -> pattern.matcher(username).matches() }) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('username').exception()
        }

        if (!allowedPatterns.any { Pattern pattern -> pattern.matcher(username).matches() }) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('username').exception()
        }
    }
}
