package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.UserEmailValidator
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserEmailValidatorImpl implements UserEmailValidator {
    @Override
    void test() {

    }
    /*
    private List<String> allowedEmailTypes
    private List<Pattern> allowedEmailPatterns
    private Integer emailMinLength
    private Integer emailMaxLength

    @Override
    void validate(UserEmail userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException('userEmail is null')
        }

        if (userEmail.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (!(userEmail.type in allowedEmailTypes)) {
            throw AppErrors.INSTANCE.fieldInvalid('type', allowedEmailTypes.join(',')).exception()
        }

        if (userEmail.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }

        if (!allowedEmailPatterns.any {
            Pattern pattern -> pattern.matcher(userEmail.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }

        if (userEmail.verified == null) {
            throw AppErrors.INSTANCE.fieldRequired('verified').exception()
        }
    }

    @Required
    void setAllowedEmailTypes(List<String> allowedEmailTypes) {
        this.allowedEmailTypes = allowedEmailTypes
    }

    @Required
    void setAllowedEmailPatterns(List<String> allowedEmailPatterns) {
        this.allowedEmailPatterns = allowedEmailPatterns.collect { String allowedEmailPattern ->
            Pattern.compile(allowedEmailPattern)
        }
    }

    @Required
    void setEmailMinLength(Integer emailMinLength) {
        this.emailMinLength = emailMinLength
    }

    @Required
    void setEmailMaxLength(Integer emailMaxLength) {
        this.emailMaxLength = emailMaxLength
    }
    */
}
