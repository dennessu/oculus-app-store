package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.core.service.validator.NickNameValidator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Check minimum and maximum nickName length
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
            throw AppCommonErrors.INSTANCE.fieldTooShort('nickName', nickNameMinLength).exception()
        }

        if (nickName.length() > nickNameMaxLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('nickName', nickNameMaxLength).exception()
        }
    }
}
