package com.junbo.crypto.core.validator

import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/13/14.
 */
@CompileStatic
interface UserCryptoValidator {
    Promise<Void> validateUserCryptoKeyCreate(UserCryptoKey userCryptoKey)
}
