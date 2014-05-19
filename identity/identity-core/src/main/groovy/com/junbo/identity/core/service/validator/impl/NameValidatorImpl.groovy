package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.DisplayNameValidator
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
    // todo:    Need to refactor according to marshall's requirement
    private Integer minFirstNameLength
    private Integer maxFirstNameLength

    private Integer minMiddleNameLength
    private Integer maxMiddleNameLength

    private Integer minLastNameLength
    private Integer maxLastNameLength

    private NickNameValidator nickNameValidator
    private DisplayNameValidator displayNameValidator

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
        if (name.firstName == null) {
            throw AppErrors.INSTANCE.fieldRequired('firstName').exception()
        }
        if (name.firstName.length() > maxFirstNameLength) {
            throw AppErrors.INSTANCE.fieldTooLong('firstName', maxFirstNameLength).exception()
        }
        if (name.firstName.length() < minFirstNameLength) {
            throw AppErrors.INSTANCE.fieldTooShort('lastName', minFirstNameLength).exception()
        }

        if (name.middleName != null) {
            if (name.middleName.length() > maxMiddleNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('middleName', maxMiddleNameLength).exception()
            }
            if (name.middleName.length() < minMiddleNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('middleName', minMiddleNameLength).exception()
            }
        }

        nickNameValidator.validateNickName(name.nickName)

        displayNameValidator.validate(name)

    }

    @Required
    void setMinFirstNameLength(Integer minFirstNameLength) {
        this.minFirstNameLength = minFirstNameLength
    }

    @Required
    void setMaxFirstNameLength(Integer maxFirstNameLength) {
        this.maxFirstNameLength = maxFirstNameLength
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
    void setMinLastNameLength(Integer minLastNameLength) {
        this.minLastNameLength = minLastNameLength
    }

    @Required
    void setMaxLastNameLength(Integer maxLastNameLength) {
        this.maxLastNameLength = maxLastNameLength
    }

    @Required
    void setNickNameValidator(NickNameValidator nickNameValidator) {
        this.nickNameValidator = nickNameValidator
    }

    @Required
    void setDisplayNameValidator(DisplayNameValidator displayNameValidator) {
        this.displayNameValidator = displayNameValidator
    }
}
