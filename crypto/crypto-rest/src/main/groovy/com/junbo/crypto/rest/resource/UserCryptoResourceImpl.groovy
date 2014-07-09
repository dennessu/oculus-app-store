/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.rest.resource

import com.junbo.crypto.core.validator.UserCryptoValidator
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.crypto.spec.resource.UserCryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 5/6/14.
 */
@CompileStatic
@Transactional
class UserCryptoResourceImpl extends CommonResourceImpl implements UserCryptoResource {

    private UserCryptoValidator userCryptoValidator

    @Override
    Promise<Void> create(UserCryptoKey userCryptoKey) {
        if (!enableUserKeyEncrypt) {
            return Promise.pure(null)
        }

        return authorize().then {
            return userCryptoValidator.validateUserCryptoKeyCreate(userCryptoKey).then {

                return getCurrentUserCryptoKey(userCryptoKey.userId).then { Integer keyVersion ->
                    return symmetricEncryptUserKey(userCryptoKey.value).then { String encryptValue ->
                        userCryptoKey.encryptValue = encryptValue
                        userCryptoKey.value = null
                        userCryptoKey.keyVersion = keyVersion + 1

                        return userCryptoKeyRepo.create(userCryptoKey).then {
                            return Promise.pure(null)
                        }
                    }
                }
            }
        }
    }

    @Required
    void setUserCryptoValidator(UserCryptoValidator userCryptoValidator) {
        this.userCryptoValidator = userCryptoValidator
    }
}
