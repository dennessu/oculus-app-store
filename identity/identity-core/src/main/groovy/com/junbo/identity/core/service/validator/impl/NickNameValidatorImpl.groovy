package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.NickNameValidator
import com.junbo.identity.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class NickNameValidatorImpl implements NickNameValidator {

    private Integer nickNameMinLength

    private Integer nickNameMaxLength

    @Required
    void setNickNameMinLength(Integer nickNameMinLength) {
        this.nickNameMinLength = nickNameMinLength
    }

    @Required
    void setNickNameMaxLength(Integer nickNameMaxLength) {
        this.nickNameMaxLength = nickNameMaxLength
    }

    void validateNickName(String nickName) {
        if (nickName == null) {
            throw new IllegalArgumentException('nickName is null')
        }

        if (nickName.length() < nickNameMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('nickName', nickNameMinLength).exception()
        }

        if (nickName.length() > nickNameMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('nickName', nickNameMaxLength).exception()
        }
    }
}
