package com.junbo.crypto.core.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.crypto.core.validator.CryptoMessageValidator
import com.junbo.crypto.spec.error.AppErrors
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class CryptoMessageValidatorImpl implements CryptoMessageValidator {

    private UserResource userResource

    private String versionSeparator

    private Boolean enableUserKeyEncrypt

    @Override
    Promise<Void> validateEncrypt(UserId userId, CryptoMessage rawMessage) {
        if (rawMessage == null) {
            throw new IllegalArgumentException('rawMessage is null')
        }
        if (rawMessage.value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
        }

        if (!enableUserKeyEncrypt) {
            return Promise.pure(null)
        }

        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound("user", userId).exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateDecrypt(UserId userId, CryptoMessage decryptMessage) {

        if (decryptMessage == null) {
            throw new IllegalArgumentException('decryptMessage is null')
        }
        if (decryptMessage.value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
        }

        String[] messageInfo = decryptMessage.value.split(versionSeparator)
        if (messageInfo == null || messageInfo.length != 2 || !(isValidInteger(messageInfo[0]))) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value').exception()
        }

        if (!enableUserKeyEncrypt) {
            return Promise.pure(null)
        }

        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return Promise.pure(null)
    }

    private boolean isValidInteger(String s) {
        if (StringUtils.isEmpty(s)) {
            return false
        }

        Integer convertedInt = null
        try {
            convertedInt = Integer.parseInt(s)
        } catch (Exception e) {
            return false
        }

        if (convertedInt > 0) {
            return true
        }
        return false
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setVersionSeparator(String versionSeparator) {
        this.versionSeparator = versionSeparator
    }

    @Required
    void setEnableUserKeyEncrypt(Boolean enableUserKeyEncrypt) {
        this.enableUserKeyEncrypt = enableUserKeyEncrypt
    }
}
