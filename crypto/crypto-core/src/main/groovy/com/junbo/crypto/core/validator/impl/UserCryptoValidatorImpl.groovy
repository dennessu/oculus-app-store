package com.junbo.crypto.core.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.crypto.core.generator.SecurityKeyGenerator
import com.junbo.crypto.core.validator.UserCryptoValidator
import com.junbo.crypto.spec.error.AppErrors
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/13/14.
 */
@CompileStatic
class UserCryptoValidatorImpl implements UserCryptoValidator {
    private UserResource userResource

    private SecurityKeyGenerator securityKeyGenerator

    @Override
    Promise<Void> validateUserCryptoKeyCreate(UserCryptoKey userCryptoKey) {
        if (userCryptoKey == null) {
            throw new IllegalArgumentException('userCryptoKey is null')
        }

        if (userCryptoKey.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        if (userCryptoKey.value != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('value').exception()
        }

        if (userCryptoKey.encryptValue != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('encryptValue').exception()
        }

        return checkBasicCryptoKey(userCryptoKey).then {
            userCryptoKey.setValue(securityKeyGenerator.generateUserKey())

            return Promise.pure(null)
        }
    }

    Promise<Void> checkBasicCryptoKey(UserCryptoKey userCryptoKey) {
        if (userCryptoKey.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("user").exception()
        }

        return userResource.get(userCryptoKey.userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound("user", userCryptoKey.userId).exception()
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setSecurityKeyGenerator(SecurityKeyGenerator securityKeyGenerator) {
        this.securityKeyGenerator = securityKeyGenerator
    }
}
