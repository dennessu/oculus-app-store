package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Check minimum and maximum givenName length
 * Check minimum and maximum middleName length
 * Check minimum and maximum familyName length
 * Check nickName valid
 * Created by kg on 3/17/14.
 */
@CompileStatic
class NameValidatorImpl implements PiiValidator {
    private Integer minMiddleNameLength
    private Integer maxMiddleNameLength

    private Integer minFamilyNameLength
    private Integer maxFamilyNameLength

    private Integer minGivenNameLength
    private Integer maxGivenNameLength

    private Integer minFullNameLength
    private Integer maxFullNameLength

    private UserService userService

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.NAME.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserName name = (UserName)JsonHelper.jsonNodeToObj(value, UserName)
        checkName(name)
        if (userId != null && !StringUtils.isEmpty(name.fullName)) {
            // If the user is deleted, no pii can be created. Only when the user is set to non-delete, he can add pii.
            return userService.getNonDeletedUser(userId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userId).exception()
                }

                return Promise.pure(null)
            }
        } else {
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserName name = (UserName)JsonHelper.jsonNodeToObj(value, UserName)
        UserName oldName = (UserName)JsonHelper.jsonNodeToObj(oldValue, UserName)

        if (name != oldName) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        return value
    }

    private void checkName(UserName name) {
        if (name.givenName != null) {
            if (name.givenName.length() > maxGivenNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.givenName', maxGivenNameLength).exception()
            }
            if (name.givenName.length() < minGivenNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.givenName', minGivenNameLength).exception()
            }
        }

        if (name.familyName != null) {
            if (name.familyName.length() > maxFamilyNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.familyName', maxFamilyNameLength).exception()
            }
            if (name.familyName.length() < minFamilyNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.familyName', minFamilyNameLength).exception()
            }
        }

        if (name.middleName != null) {
            if (name.middleName.length() > maxMiddleNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.middleName', maxMiddleNameLength).exception()
            }
            if (name.middleName.length() < minMiddleNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.middleName', minMiddleNameLength).exception()
            }
        }

        if (name.fullName != null) {
            if (name.fullName.length() > maxFullNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.fullName', maxFullNameLength).exception()
            }

            if (name.fullName.length() < minFullNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.fullName', minFullNameLength).exception()
            }
        }
    }

    @Required
    void setMinMiddleNameLength(Integer minMiddleNameLength) {
        this.minMiddleNameLength = minMiddleNameLength
    }

    @Required
    void setMaxMiddleNameLength(Integer maxMiddleNameLength) {
        this.maxMiddleNameLength = maxMiddleNameLength
    }

    @Required
    void setMinFamilyNameLength(Integer minFamilyNameLength) {
        this.minFamilyNameLength = minFamilyNameLength
    }

    @Required
    void setMaxFamilyNameLength(Integer maxFamilyNameLength) {
        this.maxFamilyNameLength = maxFamilyNameLength
    }

    @Required
    void setMinGivenNameLength(Integer minGivenNameLength) {
        this.minGivenNameLength = minGivenNameLength
    }

    @Required
    void setMaxGivenNameLength(Integer maxGivenNameLength) {
        this.maxGivenNameLength = maxGivenNameLength
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setMinFullNameLength(Integer minFullNameLength) {
        this.minFullNameLength = minFullNameLength
    }

    @Required
    void setMaxFullNameLength(Integer maxFullNameLength) {
        this.maxFullNameLength = maxFullNameLength
    }
}
