package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.UsernameValidator
import com.junbo.identity.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class UsernameValidatorImpl implements UsernameValidator {

    private String charsToDelete

    private Integer minLength

    private Integer maxLength

    private List<Pattern> disallowedPatterns

    private List<Pattern> allowedPatterns

    @Required
    void setCharsToDelete(String charsToDelete) {
        this.charsToDelete = charsToDelete
    }

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
            throw AppErrors.INSTANCE.fieldTooShort('username', minLength).exception()
        }

        if (username.length() > maxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('username', maxLength).exception()
        }

        if (disallowedPatterns.any { Pattern pattern -> pattern.matcher(username).matches() }) {
            throw AppErrors.INSTANCE.fieldInvalid('username').exception()
        }

        if (!allowedPatterns.any { Pattern pattern -> pattern.matcher(username).matches() }) {
            throw AppErrors.INSTANCE.fieldInvalid('username').exception()
        }
    }

    String normalizeUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException('username is null')
        }

        def result = StringUtils.deleteAny(username, charsToDelete)

        return result.toLowerCase()
    }
}
