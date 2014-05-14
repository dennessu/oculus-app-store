package com.junbo.crypto.core.validator

import com.junbo.common.id.UserId
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
interface CryptoMessageValidator {
    Promise<Void> validateEncrypt(UserId userId, CryptoMessage rawMessage)
    Promise<Void> validateDecrypt(UserId userId, CryptoMessage decryptMessage)
}
