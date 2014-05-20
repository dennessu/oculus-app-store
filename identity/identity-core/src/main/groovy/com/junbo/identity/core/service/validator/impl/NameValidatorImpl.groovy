package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.NickNameValidator
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class NameValidatorImpl implements PiiValidator {
    private Integer minFullNameLength
    private Integer maxFullNameLength

    private Integer minMiddleNameLength
    private Integer maxMiddleNameLength

    private Integer minFamilyNameLength
    private Integer maxFamilyNameLength

    private Integer minGivenNameLength
    private Integer maxGivenNameLength

    private NickNameValidator nickNameValidator

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.NAME.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId) {
        UserName name = (UserName)JsonHelper.jsonNodeToObj(value, UserName)

        checkName(name)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue, UserId userId) {
        UserName name = (UserName)JsonHelper.jsonNodeToObj(value, UserName)
        UserName oldName = (UserName)JsonHelper.jsonNodeToObj(oldValue, UserName)

        if (name != oldName) {
            checkName(name)
        }
        return Promise.pure(null)
    }

    private void checkName(UserName name) {
        if (name.fullName == null) {
            throw AppErrors.INSTANCE.fieldRequired('value.fullName').exception()
        }
        if (name.fullName.length() < minFullNameLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value.fullName', minFullNameLength).exception()
        }
        if (name.fullName.length() > maxFullNameLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value.fullName', maxFullNameLength).exception()
        }

        if (name.givenName != null) {
            if (name.givenName.length() > maxGivenNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value.givenName', maxGivenNameLength).exception()
            }
            if (name.givenName.length() < minGivenNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value.givenName', minGivenNameLength).exception()
            }
        }

        if (name.familyName != null) {
            if (name.familyName.length() > maxFamilyNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value.familyName', maxFamilyNameLength).exception()
            }
            if (name.familyName.length() < minFamilyNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value.familyName', minFamilyNameLength).exception()
            }
        }

        if (name.middleName != null) {
            if (name.middleName.length() > maxMiddleNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value.middleName', maxMiddleNameLength).exception()
            }
            if (name.middleName.length() < minMiddleNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value.middleName', minMiddleNameLength).exception()
            }
        }

        if (name.nickName != null) {
            nickNameValidator.validateNickName(name.nickName)
        }
    }

    @Required
    void setMinFullNameLength(Integer minFullNameLength) {
        this.minFullNameLength = minFullNameLength
    }

    @Required
    void setMaxFullNameLength(Integer maxFullNameLength) {
        this.maxFullNameLength = maxFullNameLength
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
    void setNickNameValidator(NickNameValidator nickNameValidator) {
        this.nickNameValidator = nickNameValidator
    }
}
