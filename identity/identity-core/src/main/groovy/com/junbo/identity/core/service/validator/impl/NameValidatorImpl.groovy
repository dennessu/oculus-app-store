package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.NameValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserName
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class NameValidatorImpl implements NameValidator {

    private Integer firstNameMinLength

    private Integer firstNameMaxLength

    private Integer middleNameMinLength

    private Integer middleNameMaxLength

    private Integer lastNameMinLength

    private Integer lastNameMaxLength

    private Integer honorificPrefixMinLength

    private Integer honorificPrefixMaxLength

    private Integer honorificSuffixMinLength

    private Integer honorificSuffixMaxLength

    @Required
    void setFirstNameMinLength(Integer firstNameMinLength) {
        this.firstNameMinLength = firstNameMinLength
    }

    @Required
    void setFirstNameMaxLength(Integer firstNameMaxLength) {
        this.firstNameMaxLength = firstNameMaxLength
    }

    @Required
    void setMiddleNameMinLength(Integer middleNameMinLength) {
        this.middleNameMinLength = middleNameMinLength
    }

    @Required
    void setMiddleNameMaxLength(Integer middleNameMaxLength) {
        this.middleNameMaxLength = middleNameMaxLength
    }

    @Required
    void setLastNameMinLength(Integer lastNameMinLength) {
        this.lastNameMinLength = lastNameMinLength
    }

    @Required
    void setLastNameMaxLength(Integer lastNameMaxLength) {
        this.lastNameMaxLength = lastNameMaxLength
    }

    @Required
    void setHonorificPrefixMinLength(Integer honorificPrefixMinLength) {
        this.honorificPrefixMinLength = honorificPrefixMinLength
    }

    @Required
    void setHonorificPrefixMaxLength(Integer honorificPrefixMaxLength) {
        this.honorificPrefixMaxLength = honorificPrefixMaxLength
    }

    @Required
    void setHonorificSuffixMinLength(Integer honorificSuffixMinLength) {
        this.honorificSuffixMinLength = honorificSuffixMinLength
    }

    @Required
    void setHonorificSuffixMaxLength(Integer honorificSuffixMaxLength) {
        this.honorificSuffixMaxLength = honorificSuffixMaxLength
    }

    void validateName(UserName name) {
        if (name == null) {
            throw new IllegalArgumentException('name is null')
        }

        if (name.firstName == null) {
            throw AppErrors.INSTANCE.fieldRequired('name.firstName').exception()
        }

        if (name.firstName.length() < firstNameMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('name.firstName', firstNameMinLength).exception()
        }

        if (name.firstName.length() > firstNameMaxLength) {
            throw AppErrors.INSTANCE.fieldTooShort('name.firstName', firstNameMaxLength).exception()
        }

        if (name.lastName == null) {
            throw AppErrors.INSTANCE.fieldRequired('name.lastName').exception()
        }

        if (name.lastName.length() < lastNameMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('name.lastName', lastNameMinLength).exception()
        }

        if (name.lastName.length() > lastNameMaxLength) {
            throw AppErrors.INSTANCE.fieldTooShort('name.lastName', lastNameMaxLength).exception()
        }

        if (name.middleName != null) {
            if (name.middleName.length() < middleNameMinLength) {
                throw AppErrors.INSTANCE.fieldTooShort('name.middleName', middleNameMinLength).exception()
            }

            if (name.middleName.length() > middleNameMaxLength) {
                throw AppErrors.INSTANCE.fieldTooShort('name.middleName', middleNameMaxLength).exception()
            }
        }

        if (name.honorificPrefix != null) {
            if (name.honorificPrefix.length() < honorificPrefixMinLength) {
                throw AppErrors.INSTANCE.fieldTooShort('name.honorificPrefix', honorificPrefixMinLength).exception()
            }

            if (name.honorificPrefix.length() > honorificPrefixMaxLength) {
                throw AppErrors.INSTANCE.fieldTooShort('name.honorificPrefix', honorificPrefixMaxLength).exception()
            }
        }

        if (name.honorificSuffix != null) {
            if (name.honorificSuffix.length() < honorificSuffixMinLength) {
                throw AppErrors.INSTANCE.fieldTooShort('name.honorificSuffix', honorificSuffixMinLength).exception()
            }

            if (name.honorificSuffix.length() > honorificSuffixMaxLength) {
                throw AppErrors.INSTANCE.fieldTooShort('name.honorificSuffix', honorificSuffixMaxLength).exception()
            }
        }
    }
}
