package com.junbo.crypto.core.validator.impl

import com.junbo.crypto.core.validator.MasterKeyValidator
import com.junbo.crypto.spec.error.AppErrors
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/13/14.
 */
@CompileStatic
class MasterKeyValidatorImpl implements MasterKeyValidator {

    @Override
    Promise<Void> validateMasterKeyCreate(MasterKey key) {
        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        if (key.value == null) {
            throw AppErrors.INSTANCE.fieldMissing('value').exception()
        }
        if (key.encryptValue != null) {
            throw AppErrors.INSTANCE.fieldInvalid('encryptValue is null').exception()
        }

        return Promise.pure(null)
    }
}
