package com.junbo.crypto.core.validator.impl

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

    @Override
    Promise<Void> validateUserCryptoKeyCreate(UserCryptoKey userCryptoKey) {
        if (userCryptoKey == null) {
            throw new IllegalArgumentException('userCryptoKey is null')
        }

        if (userCryptoKey.id != null) {
            throw AppErrors.INSTANCE.fieldInvalid('id is not null').exception()
        }

        if (userCryptoKey.value != null) {
            throw AppErrors.INSTANCE.fieldInvalid('value is not null').exception()
        }

        if (userCryptoKey.encryptValue != null) {
            throw AppErrors.INSTANCE.fieldInvalid('encryptValue is not null').exception()
        }

        return checkBasicCryptoKey(userCryptoKey).then {
            userCryptoKey.setValue(UUID.randomUUID().toString())

            return Promise.pure(null)
        }
    }

    Promise<Void> checkBasicCryptoKey(UserCryptoKey userCryptoKey) {
        if (userCryptoKey.userId == null) {
            throw AppErrors.INSTANCE.fieldMissing("userId").exception()
        }

        return userResource.get(userCryptoKey.userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userCryptoKey.userId).exception()
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }
}
